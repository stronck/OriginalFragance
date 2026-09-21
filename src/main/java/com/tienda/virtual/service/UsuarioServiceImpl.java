// Implementación de la lógica de registro, inicio de sesión y gestión de usuarios.
package com.tienda.virtual.service;

import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // registro
    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario iniciarSesion(String identificador, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuarioOrCorreo(identificador, identificador);
        if (usuario.isPresent() && passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
            return usuario.orElse(null);
        }
        return null;
    }

    @Override
    public List<Usuario> obtenerTodosUsuarios() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        } else {
            // Si el formulario no cambia la contraseña, conservar la contraseña
            // actualmente almacenada en la base de datos.
            Optional<Usuario> existente = usuarioRepository.findById(usuario.getId());
            existente.ifPresent(actual -> usuario.setContrasena(actual.getContrasena()));
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
