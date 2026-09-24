/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente, sus datos y su relación
 * con las demás capas de la aplicación. El código que aparece después se conserva
 * sin cambios: únicamente se agregan comentarios para facilitar su estudio y mantenimiento.
 */
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
    // Método: documenta la responsabilidad del método siguiente.
    public List<Producto> listarProductos() {
    public List<Producto> listarProductos() {
        return (List<Producto>) productoRepository.findAll();
    }
}
