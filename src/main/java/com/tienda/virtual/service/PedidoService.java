// Contrato de servicios para las operaciones relacionadas con pedidos.
package com.tienda.virtual.service;

import com.tienda.virtual.model.Pedido;
import com.tienda.virtual.model.Producto;
import com.tienda.virtual.model.Usuario;

import java.util.List;

public interface PedidoService {
    Pedido crearPedido(Usuario usuario, List<Producto> carrito);
    List<Pedido> obtenerPedidos();
    List<Pedido> obtenerPedidosPorUsuario(Long usuarioId);
    Pedido obtenerPedido(Long id);

    Pedido marcarPagoExitoso(Long id);
    byte[] generarFactura(Pedido pedido);
}
