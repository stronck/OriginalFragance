/* ===== DOCUMENTACIÓN DE USUARIOS =====
 * Este archivo controla en el navegador el registro, inicio/cierre de sesión,
 * perfil de usuario, administración de usuarios, pedidos y validaciones de sesión.
 * Los comentarios explican el flujo entre la interfaz y las API REST del backend.
 */
/*
 * DOCUMENTACIÓN DETALLADA: usuarios.js
 * Centraliza la lógica del navegador relacionada con sesión, registro, navegación,
 * perfil, usuarios y controles de acceso del panel administrativo. No se modifica
 * ninguna instrucción JavaScript; solamente se documenta el archivo.
 */
// Funciones del frontend relacionadas con sesión, usuarios, pedidos y administración.
/*
 * Autor: Camilo Tibatá Salguero
 * Proyecto: E-commerce
 * Descripción: Script.
 */

let usuario = null;

function mostrarAlerta(mensaje) {
    return new Promise((resolver) => {
        const alertaExistente = document.getElementById('alertaProduccion');
        if (alertaExistente) {
            alertaExistente.remove();
        }

        const modal = document.createElement('div');
        modal.id = 'alertaProduccion';
        modal.className = 'alerta-produccion-overlay';
        modal.setAttribute('role', 'dialog');
        modal.setAttribute('aria-modal', 'true');
        modal.setAttribute('aria-labelledby', 'alertaProduccionTitulo');

        const contenido = document.createElement('div');
        contenido.className = 'alerta-produccion';

        const encabezado = document.createElement('div');
        encabezado.className = 'alerta-produccion-header';

        const icono = document.createElement('span');
        icono.className = 'alerta-produccion-icono';
        icono.innerHTML = '<i class="fa-solid fa-circle-info"></i>';

        const titulo = document.createElement('h2');
        titulo.id = 'alertaProduccionTitulo';
        titulo.className = 'alerta-produccion-titulo';
        titulo.textContent = mensaje.toLowerCase().includes('correctamente') ||
            mensaje.toLowerCase().includes('éxito') ||
            mensaje.toLowerCase().includes('agregado') ||
            mensaje.toLowerCase().includes('exitoso')
            ? 'Operación exitosa'
            : mensaje.toLowerCase().includes('sesión')
                ? 'Sesión'
                : mensaje.toLowerCase().includes('error') ||
                  mensaje.toLowerCase().includes('no se pudo') ||
                  mensaje.toLowerCase().includes('no se pudieron')
                    ? 'No se pudo completar'
                    : 'Aviso';

        encabezado.appendChild(icono);
        encabezado.appendChild(titulo);

        const texto = document.createElement('p');
        texto.className = 'alerta-produccion-mensaje';
        texto.textContent = mensaje;

        const boton = document.createElement('button');
        boton.type = 'button';
        boton.className = 'btn alerta-produccion-boton';
        boton.textContent = 'Aceptar';

        const cerrar = () => {
            document.removeEventListener('keydown', manejarTecla);
            modal.remove();
            resolver();
        };

        const manejarTecla = (evento) => {
            if (evento.key === 'Escape') {
                cerrar();
            }
        };

        boton.addEventListener('click', cerrar);
        modal.addEventListener('click', (evento) => {
            if (evento.target === modal) {
                cerrar();
            }
        });
        document.addEventListener('keydown', manejarTecla);

        contenido.appendChild(encabezado);
        contenido.appendChild(texto);
        contenido.appendChild(boton);
        modal.appendChild(contenido);
        document.body.appendChild(modal);

        boton.focus();
    });
}


// Consulta al backend para saber qué usuario mantiene una sesión activa.
async function obtenerUsuarioSesion() {
    try {
        const response = await fetch('/api/usuarios/sesion');

        if (!response.ok) {
            usuario = null;
            return null;
        }

        usuario = await response.json();
        return usuario;
    } catch (error) {
        console.error(error);
        usuario = null;
        return null;
    }
}

async function actualizarContadorCarrito() {
    const contador = document.getElementById('contadorCarrito');
    const btnCarrito = document.getElementById('btnCarrito');

    if (!contador || !btnCarrito) {
        return;
    }

    contador.innerText = '0';
    contador.style.display = 'none';

    try {
        const response = await fetch('/api/carrito');

        if (!response.ok) {
            return;
        }

        const carrito = await response.json();
        const cantidad = carrito.length;

        contador.innerText = String(cantidad);
        contador.style.display = cantidad > 0 ? 'inline-block' : 'none';
    } catch (error) {
        console.error(error);
    }
}

// Actualiza el navbar según exista o no una sesión autenticada.
async function mostrarUsuarioNavbar() {
    const btnRegistrarse = document.getElementById('btnRegistrarse');
    const btnIniciarSesion = document.getElementById('btnIniciarSesion');
    const btnMiCuenta = document.getElementById('btnMiCuenta');
    const btnCarrito = document.getElementById('btnCarrito');
    const navbar = document.querySelector('.navbar .ms-auto');
    const datosAnteriores = document.getElementById('datosUsuarioNavbar');

    if (datosAnteriores) {
        datosAnteriores.remove();
    }

    if (btnRegistrarse) btnRegistrarse.style.display = 'none';
    if (btnIniciarSesion) btnIniciarSesion.style.display = 'none';
    if (btnMiCuenta) btnMiCuenta.style.display = 'none';
    if (btnCarrito) btnCarrito.style.display = 'none';

    const usuarioSesion = await obtenerUsuarioSesion();

    if (!navbar) {
        return;
    }

    if (usuarioSesion) {
        if (btnRegistrarse) btnRegistrarse.style.display = 'none';
        if (btnIniciarSesion) btnIniciarSesion.style.display = 'none';

        if (btnCarrito) {
            btnCarrito.style.display = 'inline-block';
            actualizarContadorCarrito();
        }

        const datosAnteriores = document.getElementById('datosUsuarioNavbar');
        if (datosAnteriores) {
            datosAnteriores.remove();
        }

        const datosNavbar = document.createElement('div');
        datosNavbar.id = 'datosUsuarioNavbar';
        datosNavbar.className = 'd-flex align-items-center';
        datosNavbar.style.cursor = 'pointer';
        datosNavbar.setAttribute('role', 'link');
        datosNavbar.setAttribute('tabindex', '0');
        datosNavbar.setAttribute('aria-label', 'Mi Cuenta');
        datosNavbar.innerHTML = `
            <i class="fa-solid fa-user-circle text-light fs-3 me-2"></i>
            <span class="me-3 fw-bold text-light">${usuarioSesion.nombreUsuario}</span>
            <button id="cerrarSesion" class="btn btn-success btn-sm" title="Cerrar Sesión" aria-label="Cerrar Sesión"><i class="fa-solid fa-right-from-bracket"></i></button>
        `;

        navbar.appendChild(datosNavbar);

        const destinoCuenta = usuarioSesion.rol === 'admin' ? 'admin.html' : 'user.html';
        datosNavbar.addEventListener('click', (evento) => {
            if (evento.target.closest('#cerrarSesion')) {
                return;
            }
            window.location.href = destinoCuenta;
        });

        datosNavbar.addEventListener('keydown', (evento) => {
            if ((evento.key === 'Enter' || evento.key === ' ') && !evento.target.closest('#cerrarSesion')) {
                evento.preventDefault();
                window.location.href = destinoCuenta;
            }
        });

        document.getElementById('cerrarSesion').addEventListener('click', async () => {
            try {
                await fetch('/api/usuarios/cerrar-sesion', {
                    method: 'POST'
                });
            } finally {
                usuario = null;

                const datosUsuarioNavbar = document.getElementById('datosUsuarioNavbar');
                const btnMiCuentaActual = document.getElementById('btnMiCuenta');
                const btnCarritoActual = document.getElementById('btnCarrito');
                const contadorCarritoActual = document.getElementById('contadorCarrito');
                const btnRegistrarseActual = document.getElementById('btnRegistrarse');
                const btnIniciarSesionActual = document.getElementById('btnIniciarSesion');

                if (datosUsuarioNavbar) datosUsuarioNavbar.remove();
                if (btnMiCuentaActual) btnMiCuentaActual.style.display = 'none';
                if (btnCarritoActual) btnCarritoActual.style.display = 'none';
                if (contadorCarritoActual) contadorCarritoActual.innerText = '0';
                if (btnRegistrarseActual) btnRegistrarseActual.style.display = 'inline-block';
                if (btnIniciarSesionActual) btnIniciarSesionActual.style.display = 'inline-block';

                window.location.replace('index.html');
            }
        });
    } else {
        if (btnRegistrarse) btnRegistrarse.style.display = 'inline-block';
        if (btnIniciarSesion) btnIniciarSesion.style.display = 'inline-block';
        if (btnMiCuenta) btnMiCuenta.style.display = 'none';
        if (btnCarrito) btnCarrito.style.display = 'none';
    }
}

mostrarUsuarioNavbar();

// Recoge el formulario de registro y lo envía al endpoint de usuarios.
async function registrarUsuario() {
    const nombreUsuario = document.getElementById('nombreUsuario').value;
    const contrasena = document.getElementById('contrasena').value;
    const confirmarContrasena = document.getElementById('confirmarContrasena').value;
    const nombres = document.getElementById('nombres').value;
    const apellidos = document.getElementById('apellidos').value;
    const celular = document.getElementById('celular').value;
    const correo = document.getElementById('correo').value;
    const direccionEnvio = document.getElementById('direccionEnvio').value;

    if (contrasena !== confirmarContrasena) {
        await mostrarAlerta('Las contraseñas no coinciden.');
        return;
    }

    const nuevoUsuario = {
        nombreUsuario,
        contrasena,
        nombres,
        apellidos,
        celular,
        correo,
        direccionEnvio
    };

    const response = await fetch('/api/usuarios/registrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoUsuario)
    });

    if (response.ok) {
        await mostrarAlerta('Usuario registrado correctamente');
        window.location.href = 'iniciarsesion.html';
    } else {
        await mostrarAlerta('Error al registrar el usuario');
    }
}

const registroForm = document.getElementById('registroForm');
if (registroForm) {
    registroForm.addEventListener('submit', (e) => {
        e.preventDefault();
        registrarUsuario();
    });
}

// Envía las credenciales al backend y redirige según el rol recibido.
async function iniciarSesion() {
    const nombreUsuario = document.getElementById('nombreUsuario').value;
    const contrasena = document.getElementById('contrasena').value;

    const response = await fetch('/api/usuarios/iniciar-sesion', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombreUsuario, contrasena })
    });

    if (!response.ok) {
        await mostrarAlerta('Credenciales incorrectas o usuario no existe en la base de datos');
        return;
    }

    const usuarioAutenticado = await response.json();

    if (usuarioAutenticado && usuarioAutenticado.rol) {
        usuario = usuarioAutenticado;
        window.location.href = usuarioAutenticado.rol.toLowerCase() === 'admin'
            ? 'admin.html'
            : 'user.html';
    } else {
        await mostrarAlerta('Error al iniciar sesión: usuario sin rol');
    }
}

const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        iniciarSesion();
    });
}

if (window.location.pathname.endsWith('admin.html')) {
    // Solicita al backend la lista de usuarios para el panel administrativo.
async function cargarUsuarios() {
        const response = await fetch('/api/usuarios');

        if (response.status === 401 || response.status === 403) {
            window.location.replace('index.html');
            return;
        }

        if (!response.ok) {
            await mostrarAlerta('No se pudieron cargar los usuarios');
            return;
        }

        const usuarios = await response.json();
        const table = document.getElementById('usuariosTable');

        if (table) {
            table.innerHTML = '';

            usuarios.forEach(user => {
                table.innerHTML += `
                    <tr>
                        <td>${user.id ?? ''}</td>
                        <td>${user.nombreUsuario ?? ''}</td>
                        <td>${user.nombres ?? ''}</td>
                        <td>${user.apellidos ?? ''}</td>
                        <td>${user.celular ?? ''}</td>
                        <td>${user.correo ?? ''}</td>
                        <td>${user.direccionEnvio ?? ''}</td>
                        <td>${user.rol ?? ''}</td>
                    </tr>
                `;
            });
        }
    }

    cargarUsuarios();
    cargarPedidos();
}

// Carga los datos del usuario autenticado en su formulario de perfil.
async function cargarDatosUsuario() {
    if (!window.location.pathname.endsWith('user.html')) {
        return;
    }

    const usuarioSesion = await obtenerUsuarioSesion();

    if (!usuarioSesion) {
        window.location.replace('index.html');
        return;
    }

    const eliminarCuentaBtn = document.getElementById('eliminarCuenta');
    if (eliminarCuentaBtn) {
        eliminarCuentaBtn.style.display = usuarioSesion.rol?.toLowerCase() === 'admin' ? 'none' : 'inline-block';
    }

    document.getElementById('nombreUsuario').value = usuarioSesion.nombreUsuario;
    document.getElementById('nombres').value = usuarioSesion.nombres;
    document.getElementById('apellidos').value = usuarioSesion.apellidos;
    document.getElementById('celular').value = usuarioSesion.celular;
    document.getElementById('correo').value = usuarioSesion.correo;
    document.getElementById('direccionEnvio').value = usuarioSesion.direccionEnvio;
}

cargarDatosUsuario();

const eliminarCuentaBtn = document.getElementById('eliminarCuenta');
if (eliminarCuentaBtn) {
    eliminarCuentaBtn.addEventListener('click', async () => {
        if (!usuario) {
            usuario = await obtenerUsuarioSesion();
        }

        if (!usuario) {
            window.location.replace('index.html');
            return;
        }

        const response = await fetch(`/api/usuarios/${usuario.id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            usuario = null;
            await mostrarAlerta('Cuenta eliminada con éxito');
            window.location.replace('index.html');
        } else if (response.status === 401 || response.status === 403) {
            await mostrarAlerta('No tienes permiso para eliminar esta cuenta');
            window.location.replace('index.html');
        } else {
            await mostrarAlerta('No se pudo eliminar la cuenta');
        }
    });
}

const actualizarDatosBtn = document.getElementById('actualizarDatos');
const guardarCambiosBtn = document.getElementById('guardarCambios');
const passwordFields = document.getElementById('passwordFields');

if (actualizarDatosBtn && guardarCambiosBtn) {
    actualizarDatosBtn.addEventListener('click', () => {
        document.querySelectorAll('#updateForm input').forEach(input => {
            input.disabled = false;
        });

        if (passwordFields) {
            passwordFields.style.display = 'block';
        }

        actualizarDatosBtn.style.display = 'none';
        guardarCambiosBtn.style.display = 'block';
    });

    guardarCambiosBtn.addEventListener('click', async () => {
        const inputs = document.querySelectorAll('#updateForm input');

        for (const input of inputs) {
            if (!input.disabled && !input.value.trim()) {
                await mostrarAlerta('Todos los campos deben estar llenos, incluida la contraseña y su confirmación.');
                input.focus();
                return;
            }
        }

        const nuevaContrasena = document.getElementById('nuevaContrasena').value.trim();
        const confirmarContrasena = document.getElementById('confirmarContrasena').value.trim();

        if (!nuevaContrasena || !confirmarContrasena) {
            await mostrarAlerta('Debes ingresar la contraseña y la confirmación de contraseña.');
            if (!nuevaContrasena) {
                document.getElementById('nuevaContrasena').focus();
            } else {
                document.getElementById('confirmarContrasena').focus();
            }
            return;
        }

        if (nuevaContrasena !== confirmarContrasena) {
            await mostrarAlerta('Las contraseñas no coinciden.');
            return;
        }

        const usuarioActualizado = {
            nombreUsuario: document.getElementById('nombreUsuario').value,
            nombres: document.getElementById('nombres').value,
            apellidos: document.getElementById('apellidos').value,
            celular: document.getElementById('celular').value,
            correo: document.getElementById('correo').value,
            direccionEnvio: document.getElementById('direccionEnvio').value
        };

        if (nuevaContrasena) {
            usuarioActualizado.contrasena = nuevaContrasena;
        }

        const response = await fetch('/api/usuarios', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuarioActualizado)
        });

        if (response.status === 401 || response.status === 403) {
            await mostrarAlerta('La sesión ha terminado o no tienes permiso para actualizar estos datos.');
            window.location.replace('index.html');
            return;
        }

        if (response.ok) {
            usuario = await response.json();
            await mostrarAlerta('Datos actualizados correctamente');
            window.location.reload();
        } else {
            await mostrarAlerta('Error al actualizar los datos');
        }
    });
}

const esPaginaPrivada = window.location.pathname.endsWith('admin.html') ||
                        window.location.pathname.endsWith('carrito.html');

if (esPaginaPrivada) {
    window.addEventListener('pagehide', () => {
        document.documentElement.classList.add('auth-pending');
    });

    window.addEventListener('pageshow', async (event) => {
        if (!event.persisted) {
            return;
        }

        document.documentElement.classList.add('auth-pending');

        try {
            const response = await fetch('/api/usuarios/sesion');

            if (!response.ok) {
                window.location.replace('index.html');
                return;
            }

            if (window.location.pathname.endsWith('admin.html')) {
                const usuarioSesion = await response.json();

                if (!usuarioSesion || usuarioSesion.rol?.toLowerCase() !== 'admin') {
                    window.location.replace('index.html');
                    return;
                }
            }

            document.documentElement.classList.remove('auth-pending');
        } catch (error) {
            window.location.replace('index.html');
        }
    });
}

const esPaginaPublica = !esPaginaPrivada;

if (esPaginaPublica) {
    window.addEventListener('pagehide', () => {
        const navbar = document.querySelector('.navbar .ms-auto');

        if (navbar) {
            navbar.style.visibility = 'hidden';
        }

        usuario = null;

        const datosUsuarioNavbar = document.getElementById('datosUsuarioNavbar');
        const btnMiCuenta = document.getElementById('btnMiCuenta');
        const btnCarrito = document.getElementById('btnCarrito');
        const contadorCarrito = document.getElementById('contadorCarrito');

        if (datosUsuarioNavbar) datosUsuarioNavbar.remove();
        if (btnMiCuenta) btnMiCuenta.style.display = 'none';
        if (btnCarrito) btnCarrito.style.display = 'none';
        if (contadorCarrito) contadorCarrito.innerText = '0';
    });

    window.addEventListener('pageshow', async (event) => {
        if (!event.persisted) {
            return;
        }

        usuario = null;
        await mostrarUsuarioNavbar();

        const navbar = document.querySelector('.navbar .ms-auto');

        if (navbar) {
            navbar.style.visibility = 'visible';
        }
    });
}

// Carga los pedidos disponibles para administración y construye la tabla.
async function cargarPedidos() {
    const response = await fetch('/api/pedidos');

    if (response.status === 401 || response.status === 403) {
        window.location.replace('index.html');
        return;
    }

    if (!response.ok) {
        await mostrarAlerta('No se pudieron cargar los pedidos');
        return;
    }

    const pedidos = await response.json();
    const table = document.getElementById('pedidosTable');

    if (!table) {
        return;
    }

    table.innerHTML = '';

    pedidos.forEach(pedido => {
        const usuarioPedido = pedido.usuario || {};
        const fecha = pedido.fecha || '';
        const fechaFormateada = fecha
            ? fecha.substring(0, 10).split('-').reverse().join('/') + ' ' + fecha.substring(11, 16)
            : '';

        table.innerHTML += `
            <tr>
                <td>Pedido ${pedido.id}</td>
                <td>${usuarioPedido.id ?? ''}</td>
                <td>${usuarioPedido.nombreUsuario ?? ''}</td>
                <td>${usuarioPedido.nombres ?? ''}</td>
                <td>${usuarioPedido.apellidos ?? ''}</td>
                <td>${usuarioPedido.celular ?? ''}</td>
                <td>${usuarioPedido.correo ?? ''}</td>
                <td>${usuarioPedido.direccionEnvio ?? ''}</td>
                <td>${Number(pedido.total ?? 0).toLocaleString('es-CO')}</td>
                <td>${pedido.estado || ''}</td>
                <td>${fechaFormateada}</td>
                <td>${pedido.detalleCompra ?? ''}</td>
                <td>
                    ${pedido.estado?.toLowerCase() === 'pendiente'
                        ? '<button class="btn btn-success btn-sm" onclick="confirmarPagoPedido(' + pedido.id + ')">Pago exitoso e imprimir factura</button>'
                        : 'Pago confirmado'}
                </td>
            </tr>
        `;
    });
}

// Confirma el pago de un pedido y solicita la generación de su factura.
async function confirmarPagoPedido(id) {
    try {
        const response = await fetch('/api/pedidos/' + id + '/pago-exitoso', {
            method: 'POST'
        });

        if (response.status === 401 || response.status === 403) {
            await mostrarAlerta('No tienes permiso para cambiar el estado del pedido');
            window.location.replace('index.html');
            return;
        }

        if (response.status === 404) {
            await mostrarAlerta('Pedido no encontrado');
            return;
        }

        if (!response.ok) {
            await mostrarAlerta('No se pudo cambiar el estado del pedido ni generar la factura');
            return;
        }

        await response.json();

        const ventana = window.open('/api/pedidos/' + id + '/factura', '_blank');

        if (!ventana) {
            await mostrarAlerta('Permite las ventanas emergentes para imprimir la factura.');
        }

        await mostrarAlerta('Pago exitoso y factura generada');
        await cargarPedidos();
    } catch (error) {
        console.error(error);
        await mostrarAlerta('Error al cambiar el estado del pedido');
    }
}

// Obtiene y muestra únicamente los pedidos asociados al usuario actual.
async function cargarMisPedidos() {
    if (!window.location.pathname.endsWith('user.html')) {
        return;
    }

    const contenedor = document.getElementById('listaPedidos');
    if (!contenedor) {
        return;
    }

    try {
        const response = await fetch('/api/pedidos/mis-pedidos');

        if (response.status === 401 || response.status === 403) {
            window.location.replace('index.html');
            return;
        }

        if (!response.ok) {
            contenedor.innerHTML = '<p>No se pudieron cargar tus pedidos.</p>';
            return;
        }

        const pedidos = await response.json();
        contenedor.innerHTML = '';

        if (pedidos.length === 0) {
            contenedor.innerHTML = '<p>No tienes pedidos registrados.</p>';
            return;
        }

        pedidos.forEach(pedido => {
            const fila = document.createElement('div');
            fila.className = 'd-flex justify-content-center align-items-center gap-3 mb-3';

            const fecha = pedido.fecha || '';
            const fechaFormateada = fecha
                ? fecha.substring(0, 10).split('-').reverse().join('/') + ' ' + fecha.substring(11, 16)
                : 'Fecha no disponible';

            fila.innerHTML = `
                <span>Fecha de compra: ${fechaFormateada}</span>
                ${pedido.estado?.toLowerCase() === 'pago exitoso'
                    ? '<button class="btn btn-success btn-sm" onclick="imprimirFacturaUsuario(' + pedido.id + ')">Imprimir factura</button>'
                    : '<span class="text-light fw-bold">Pendiente Confirmación de pago</span>'}
            `;
            contenedor.appendChild(fila);
        });
    } catch (error) {
        console.error(error);
        contenedor.innerHTML = '<p>No se pudieron cargar tus pedidos.</p>';
    }
}

// Abre la factura PDF del pedido autorizado en una nueva ventana.
async function imprimirFacturaUsuario(id) {
    const ventana = window.open('/api/pedidos/' + id + '/factura', '_blank');

    if (!ventana) {
        await mostrarAlerta('Permite las ventanas emergentes para imprimir la factura.');
        return;
    }

    const verificarCierre = setInterval(() => {
        if (ventana.closed) {
            clearInterval(verificarCierre);
            window.location.replace('index.html');
        }
    }, 500);
}


cargarMisPedidos();
