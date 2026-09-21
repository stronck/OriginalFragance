// Repositorio para acceder a los productos almacenados en la base de datos.
package com.tienda.virtual.repository;

import com.tienda.virtual.model.Producto;
import org.springframework.data.repository.CrudRepository;

public interface ProductoRepository extends CrudRepository<Producto, Long> {
}
