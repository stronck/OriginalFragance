/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente y el flujo de datos que implementa.
 * Todo el código funcional existente se conserva sin cambios; solamente se agregan comentarios.
 */
// Implementación de la lógica de negocio de los pedidos y sus facturas.
package com.tienda.virtual.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.virtual.model.Pedido;
import com.tienda.virtual.model.Producto;
import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoService carritoService;

    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    private final ObjectMapper objectMapper = new ObjectMapper();private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Pedido crearPedido(Usuario usuario, List<Producto> carrito) {public Pedido crearPedido(Usuario usuario, List<Producto> carrito) {
        if (usuario == null || carrito == null || carrito.isEmpty()) {
            return null;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Producto producto : carrito) {
            if (producto.getPrecio() != null) {
                total = total.add(producto.getPrecio());
            }
        }

        try {
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setTotal(total);
            pedido.setEstado("pendiente");
            pedido.setFecha(LocalDateTime.now(ZoneId.of("America/Bogota")));
            pedido.setDetalleCompra(objectMapper.writeValueAsString(carrito));

            return pedidoRepository.save(pedido);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo crear el pedido", e);
        }
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public List<Pedido> obtenerPedidos() {public List<Pedido> obtenerPedidos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public List<Pedido> obtenerPedidosPorUsuario(Long usuarioId) {public List<Pedido> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Pedido obtenerPedido(Long id) {public Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Pedido marcarPagoExitoso(Long id) {public Pedido marcarPagoExitoso(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido == null) {
            return null;
        }

        pedido.setEstado("pago exitoso");
        return pedidoRepository.save(pedido);
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public byte[] generarFactura(Pedido pedido) {public byte[] generarFactura(Pedido pedido) {
        if (pedido == null || pedido.getDetalleCompra() == null) {
            return null;
        }

        try {
            List<Producto> productos = objectMapper.readValue(
                    pedido.getDetalleCompra(),
                    new TypeReference<List<Producto>>() {}
            );

            return carritoService.generarFactura(productos, pedido.getUsuario(), pedido.getId(), pedido.getEstado(), pedido.getFecha());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
