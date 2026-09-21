// Repositorio que permite consultar y persistir pedidos mediante Spring Data.
package com.tienda.virtual.repository;

import com.tienda.virtual.model.Pedido;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PedidoRepository extends CrudRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
