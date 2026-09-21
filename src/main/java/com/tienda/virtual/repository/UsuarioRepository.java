// Repositorio para consultar y administrar usuarios mediante Spring Data.
package com.tienda.virtual.repository;

import com.tienda.virtual.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    // optional es un envoltorio que puede o no contener un valor no null
    Optional<Usuario> findByNombreUsuario(String nombreUsuario); // Se usa en iniciarSesion
    Optional<Usuario> findByNombreUsuarioOrCorreo(String nombreUsuario, String correo);
}