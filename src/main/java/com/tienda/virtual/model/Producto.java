// Modelo de datos que representa un producto disponible en la tienda.
package com.tienda.virtual.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private BigDecimal precio; // BigDecimal para formatear precio a miles (bd decimal)
    private String descripcion;
}
