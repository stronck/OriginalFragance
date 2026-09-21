// Modelo de datos que representa un pedido almacenado en la base de datos.
package com.tienda.virtual.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private BigDecimal total;
    private String estado;
    private LocalDateTime fecha;
    @Column(columnDefinition = "TEXT")
    private String detalleCompra;
}
