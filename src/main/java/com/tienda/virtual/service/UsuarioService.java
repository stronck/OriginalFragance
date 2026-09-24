/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente y el flujo de datos que implementa.
 * Todo el código funcional existente se conserva sin cambios; solamente se agregan comentarios.
 */
// Contrato de servicios para las operaciones relacionadas con usuarios.
package com.tienda.virtual.service;

import com.tienda.virtual.model.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario registrarUsuario(Usuario usuario);

    Usuario iniciarSesion(String identificador, String contrasena);

    List<Usuario> obtenerTodosUsuarios();

    void eliminarUsuario(Long id);

    Usuario actualizarUsuario(Usuario usuario);

    Usuario obtenerPorId(Long id);
}
