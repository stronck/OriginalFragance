// Funciones del catálogo y carrito: consulta productos, administra la sesión y gestiona pedidos.
/* Carrito administrado por el backend mediante HttpSession. */

async function cargarProductos() {
    try {
        const res = await fetch('/api/productos');

        if (!res.ok) {
            throw new Error('No se pudieron cargar los productos');
        }

        const productos = await res.json();
        const contenedor = document.getElementById('productos');

        if (!contenedor) {
            return;
        }

        const imagenes = ["img/fame.JPEG", "img/xsblack.JPEG", "img/lady.JPEG", "img/xs.JPEG"];

        productos.forEach((p, index) => {
            const card = document.createElement('div');
            card.className = 'd-flex';

            const imagenSrc = imagenes[index % imagenes.length];
            const precioFormateado = new Intl.NumberFormat('es-ES').format(p.precio);

            card.innerHTML = `
                <div class="card mb-4">
                    <img src="${imagenSrc}" class="card-img-top">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${p.nombre}</h5>
                        <p class="card-text">${p.descripcion}</p>
                        <p class="fw-bold">Precio $${precioFormateado}</p>
                        <div class="d-grid gap-2">
                            <button class="btn btn-primary" onclick='agregar(${JSON.stringify(p)})'>Agregar al Carrito</button>
                            <button class="btn btn-success" onclick='irAlCarrito()'>Ir al Carrito</button>
                        </div>
                    </div>
                </div>
            `;

            contenedor.appendChild(card);
        });
    } catch (error) {
        console.error(error);
        await mostrarAlerta('No se pudieron cargar los productos.');
    }
}

async function obtenerSesion() {
    const res = await fetch('/api/usuarios/sesion');

    if (!res.ok) {
        return null;
    }

    return await res.json();
}

async function agregar(producto) {
    try {
        const sesion = await obtenerSesion();

        if (!sesion) {
            await mostrarAlerta('Para comprar debe iniciar sesión');
            window.location.href = 'iniciarsesion.html';
            return;
        }

        const res = await fetch('/api/carrito/agregar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: producto.id })
        });

        if (res.status === 401) {
            await mostrarAlerta('La sesión ha terminado. Inicia sesión nuevamente.');
            window.location.href = 'iniciarsesion.html';
            return;
        }

        if (!res.ok) {
            await mostrarAlerta('No se pudo agregar el producto al carrito');
            return;
        }

        await mostrarAlerta('Producto agregado');
        if (typeof actualizarContadorCarrito === 'function') {
            await actualizarContadorCarrito();
        }
    } catch (error) {
        console.error(error);
        await mostrarAlerta('Error al agregar el producto al carrito');
    }
}

async function irAlCarrito() {
    const sesion = await obtenerSesion();

    if (sesion) {
        window.location.href = 'carrito.html';
    } else {
        await mostrarAlerta('Para comprar debe iniciar sesión');
        window.location.href = 'iniciarsesion.html';
    }
}

async function mostrarCarrito() {
    const lista = document.getElementById('listaCarrito');
    const totalElemento = document.getElementById('total');

    if (!lista || !totalElemento) {
        return;
    }

    try {
        const res = await fetch('/api/carrito');

        if (res.status === 401) {
            window.location.replace('index.html');
            return;
        }

        if (!res.ok) {
            throw new Error('No se pudo obtener el carrito');
        }

        const carrito = await res.json();

        lista.innerHTML = '';
        let total = 0;

        carrito.forEach((p, index) => {
            const item = document.createElement('div');
            item.className = "d-flex align-items-center justify-content-between border p-2 mb-2";

            const precioFormateado = new Intl.NumberFormat('es-ES').format(p.precio);

            item.innerHTML = `
                <p class="m-0">${p.nombre} - $${precioFormateado}</p>
                <button class="btn btn-danger btn-sm" onclick="quitarDelCarrito(${index})">
                    ❌ Quitar del Carrito
                </button>
            `;

            lista.appendChild(item);
            total += Number(p.precio);
        });

        const totalFormateado = new Intl.NumberFormat('es-ES').format(total);
        totalElemento.innerText = 'Precio Total a Pagar: $' + totalFormateado;
    } catch (error) {
        console.error(error);
        await mostrarAlerta('No se pudo cargar el carrito.');
    }
}

async function quitarDelCarrito(index) {
    try {
        const res = await fetch('/api/carrito/' + index, {
            method: 'DELETE'
        });

        if (res.status === 401) {
            await mostrarAlerta('La sesión ha terminado. Inicia sesión nuevamente.');
            window.location.replace('iniciarsesion.html');
            return;
        }

        if (!res.ok) {
            await mostrarAlerta('No se pudo quitar el producto');
            return;
        }

        await mostrarCarrito();
        if (typeof actualizarContadorCarrito === 'function') {
            await actualizarContadorCarrito();
        }
    } catch (error) {
        console.error(error);
        await mostrarAlerta('Error al quitar el producto del carrito');
    }
}

async function pagar() {
    try {
        const res = await fetch('/api/pedidos', {
            method: 'POST'
        });

        if (res.status === 401) {
            await mostrarAlerta('La sesión ha terminado. Inicia sesión nuevamente.');
            window.location.replace('iniciarsesion.html');
            return;
        }

        if (res.status === 204) {
            await mostrarAlerta('Carrito vacío');
            return;
        }

        if (!res.ok) {
            await mostrarAlerta('Error al crear el pedido');
            return;
        }

        await mostrarAlerta('En breve se confirmará el pago y se guardará la factura de compra en tu cuenta de usuario en la sección Mis Pedidos, la transportadora se comunicará contigo para la entrega');

        const lista = document.getElementById('listaCarrito');
        const totalElemento = document.getElementById('total');

        if (lista) {
            lista.innerHTML = '';
        }

        if (totalElemento) {
            totalElemento.innerText = 'Precio Total a Pagar: $0';
        }

        if (typeof actualizarContadorCarrito === 'function') {
            await actualizarContadorCarrito();
        }

        window.location.reload();
    } catch (error) {
        console.error(error);
        await mostrarAlerta('Error al crear el pedido');
    }
}

if (document.getElementById('productos')) {
    cargarProductos();
}

if (document.getElementById('listaCarrito')) {
    mostrarCarrito();
}
