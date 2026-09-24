/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente, sus datos y su relación
 * con las demás capas de la aplicación. El código que aparece después se conserva
 * sin cambios: únicamente se agregan comentarios para facilitar su estudio y mantenimiento.
 */
// Repositorio que permite consultar y persistir pedidos mediante Spring Data.
package com.tienda.virtual.repository;

import com.tienda.virtual.model.Pedido;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PedidoRepository extends CrudRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
