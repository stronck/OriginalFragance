// Contrato de servicios relacionado con la generación de facturas del carrito.
package com.tienda.virtual.service;

import com.tienda.virtual.model.Producto;
import com.tienda.virtual.model.Usuario;
import java.util.List;


public interface CarritoService {
    byte[] generarFactura(List<Producto> carrito, Usuario usuario, Long pedidoId, String pedidoEstado, java.time.LocalDateTime pedidoFecha);
}
