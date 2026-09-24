/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente, sus datos y su relación
 * con las demás capas de la aplicación. El código que aparece después se conserva
 * sin cambios: únicamente se agregan comentarios para facilitar su estudio y mantenimiento.
 */
// Modelo de datos que representa la información de un usuario de la tienda.
package com.tienda.virtual.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreUsuario;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;
    private String nombres;
    private String apellidos;
    private String celular;
    private String correo;
    private String direccionEnvio;
    private String rol = "user"; // Por defecto es "user"
}
