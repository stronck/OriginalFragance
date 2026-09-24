/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente y el flujo de datos que implementa.
 * Todo el código funcional existente se conserva sin cambios; solamente se agregan comentarios.
 */
// Contrato de servicios relacionado con la generación de facturas del carrito.
package com.tienda.virtual.service;

import com.tienda.virtual.model.Producto;
import com.tienda.virtual.model.Usuario;
import java.util.List;


public interface CarritoService {
    byte[] generarFactura(List<Producto> carrito, Usuario usuario, Long pedidoId, String pedidoEstado, java.time.LocalDateTime pedidoFecha);
}
