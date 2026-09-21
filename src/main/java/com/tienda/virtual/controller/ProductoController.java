// Controlador REST que expone las operaciones disponibles para consultar productos.
package com.tienda.virtual.controller;

import com.tienda.virtual.model.Producto;
import com.tienda.virtual.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// llama la funcion listarProductos y Devuelve todos los productos(bd) a fetch por la url /api/productos
@RequestMapping("/api/productos")
public class ProductoController {

    // no se implemento service y serviceImpl para productos porque solo es un metodo listar del CRUD
    @Autowired
    private ProductoRepository productoRepository;


    @GetMapping
    public List<Producto> listarProductos() {
        return (List<Producto>) productoRepository.findAll();
    }
}
