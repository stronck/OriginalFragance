/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente y el flujo de datos que implementa.
 * Todo el código funcional existente se conserva sin cambios; solamente se agregan comentarios.
 */
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

    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // registro
    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Usuario registrarUsuario(Usuario usuario) {public Usuario registrarUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Usuario iniciarSesion(String identificador, String contrasena) {public Usuario iniciarSesion(String identificador, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByNombreUsuarioOrCorreo(identificador, identificador);
        if (usuario.isPresent() && passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
            return usuario.orElse(null);
        }
        return null;
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public List<Usuario> obtenerTodosUsuarios() {public List<Usuario> obtenerTodosUsuarios() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public void eliminarUsuario(Long id) {public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Usuario actualizarUsuario(Usuario usuario) {public Usuario actualizarUsuario(Usuario usuario) {
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

    // Método: implementa la operación indicada por su firma y conecta este componente con el flujo de la aplicación.
    public Usuario obtenerPorId(Long id) {public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
