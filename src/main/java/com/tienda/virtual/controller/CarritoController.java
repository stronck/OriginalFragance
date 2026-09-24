/*
 * DOCUMENTACIÓN DEL ARCHIVO: CarritoController.java
 *
 * Este bloque explica el propósito general del archivo sin modificar su lógica.
 * Las clases, métodos, atributos, anotaciones y llamadas que siguen pertenecen
 * a la implementación funcional de la aplicación y se conservan exactamente.
 * La información detallada se centra en qué responsabilidad cumple cada parte,
 * cómo participa en el flujo de la tienda y qué relación tiene con las demás capas.
 */
// Controlador REST encargado de gestionar el carrito asociado a la sesión del usuario.
package com.tienda.virtual.controller;

import com.tienda.virtual.model.Producto;
import com.tienda.virtual.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private static final String USUARIO_ID = "usuarioId";
    private static final String CARRITO = "carrito";

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping("/agregar")
    public ResponseEntity<List<Producto>> agregarProducto(
            @RequestBody Producto productoSolicitado,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (productoSolicitado == null || productoSolicitado.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // El navegador solamente envía el id. El backend obtiene nombre, precio
        // y descripción directamente de la BD para evitar confiar en precios
        // modificados desde JavaScript.
        Producto producto = productoRepository.findById(productoSolicitado.getId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        List<Producto> carrito = carritoEnSesion(session);
        carrito.add(producto);
        session.setAttribute(CARRITO, carrito);

        return ResponseEntity.ok(carrito);
    }

    @GetMapping
    // Método: documenta la responsabilidad del método siguiente.
    public ResponseEntity<List<Producto>> obtenerCarrito(HttpSession session) {
    public ResponseEntity<List<Producto>> obtenerCarrito(HttpSession session) {
        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(carritoEnSesion(session));
    }

    @DeleteMapping("/{indice}")
    public ResponseEntity<List<Producto>> quitarProducto(
            @PathVariable int indice,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Producto> carrito = carritoEnSesion(session);

        if (indice < 0 || indice >= carrito.size()) {
            return ResponseEntity.notFound().build();
        }

        carrito.remove(indice);

        if (carrito.isEmpty()) {
            session.removeAttribute(CARRITO);
        } else {
            session.setAttribute(CARRITO, carrito);
        }

        return ResponseEntity.ok(carrito);
    }

    // Método: documenta la responsabilidad del método siguiente.
    private List<Producto> carritoEnSesion(HttpSession session) {
    private List<Producto> carritoEnSesion(HttpSession session) {
        Object valor = session.getAttribute(CARRITO);

        if (valor instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Producto> carrito = (List<Producto>) valor;
            return carrito;
        }

        return new ArrayList<>();
    }

    // Método: documenta la responsabilidad del método siguiente.
    private boolean usuarioAutenticado(HttpSession session) {
    private boolean usuarioAutenticado(HttpSession session) {
        return session.getAttribute(USUARIO_ID) != null;
    }
}
