/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente, sus datos y su relación
 * con las demás capas de la aplicación. El código que aparece después se conserva
 * sin cambios: únicamente se agregan comentarios para facilitar su estudio y mantenimiento.
 */
// Controlador REST encargado de crear, consultar, actualizar y generar facturas de pedidos.
package com.tienda.virtual.controller;

import com.tienda.virtual.model.Pedido;
import com.tienda.virtual.model.Producto;
import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final String USUARIO_ID = "usuarioId";
    private static final String USUARIO = "usuario";
    private static final String CARRITO = "carrito";

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.public ResponseEntity<Pedido> crearPedido(HttpSession session) {
    public ResponseEntity<Pedido> crearPedido(HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Producto> carrito = carritoEnSesion(session);
        if (carrito.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Pedido pedido = pedidoService.crearPedido(usuario, carrito);
        if (pedido == null) {
            return ResponseEntity.internalServerError().build();
        }

        session.removeAttribute(CARRITO);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/mis-pedidos")
    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.public ResponseEntity<List<Pedido>> obtenerMisPedidos(HttpSession session) {
    public ResponseEntity<List<Pedido>> obtenerMisPedidos(HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(usuario.getId()));
    }

    @GetMapping("/{id}/factura")
    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.public ResponseEntity<byte[]> generarFactura(@PathVariable Long id, HttpSession session) {
    public ResponseEntity<byte[]> generarFactura(@PathVariable Long id, HttpSession session) {
        Usuario usuarioSesion = usuarioDeSesion(session);

        if (usuarioSesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Pedido pedido = pedidoService.obtenerPedido(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        boolean administrador = esAdministrador(session);
        boolean propietario = pedido.getUsuario() != null
                && pedido.getUsuario().getId().equals(usuarioSesion.getId());

        if (!administrador && !propietario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!administrador && !"pago exitoso".equalsIgnoreCase(pedido.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        byte[] pdf = pedidoService.generarFactura(pedido);
        if (pdf == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=factura-pedido-" + pedido.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping
    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.public ResponseEntity<List<Pedido>> obtenerPedidos(HttpSession session) {
    public ResponseEntity<List<Pedido>> obtenerPedidos(HttpSession session) {
        if (!esAdministrador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(pedidoService.obtenerPedidos());
    }

    @PostMapping("/{id}/pago-exitoso")
    public ResponseEntity<Pedido> marcarPagoExitoso(
            @PathVariable Long id,
            HttpSession session) {

        if (!esAdministrador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pedido pedido = pedidoService.marcarPagoExitoso(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pedido);
    }

    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.private boolean esAdministrador(HttpSession session) {
    private boolean esAdministrador(HttpSession session) {
        Usuario usuario = usuarioDeSesion(session);
        return usuario != null && "admin".equalsIgnoreCase(usuario.getRol());
    }

    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.private Usuario usuarioDeSesion(HttpSession session) {
    private Usuario usuarioDeSesion(HttpSession session) {
        Object usuario = session.getAttribute(USUARIO);
        if (usuario instanceof Usuario) {
            return (Usuario) usuario;
        }

        return null;
    }

    // Método: define una operación del componente y concentra la responsabilidad indicada por su firma.private List<Producto> carritoEnSesion(HttpSession session) {
    private List<Producto> carritoEnSesion(HttpSession session) {
        Object valor = session.getAttribute(CARRITO);

        if (valor instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Producto> carrito = (List<Producto>) valor;
            return carrito;
        }

        return new ArrayList<>();
    }
}
