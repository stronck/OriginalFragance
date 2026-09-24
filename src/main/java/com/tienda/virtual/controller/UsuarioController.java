/*
 * DOCUMENTACIÓN DETALLADA DEL ARCHIVO
 * Este comentario explica la responsabilidad del componente, sus datos y su relación
 * con las demás capas de la aplicación. El código que aparece después se conserva
 * sin cambios: únicamente se agregan comentarios para facilitar su estudio y mantenimiento.
 */
// Controlador REST encargado del registro, autenticación y gestión del perfil de usuarios.
package com.tienda.virtual.controller;

import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final String USUARIO_ID = "usuarioId";
    private static final String USUARIO = "usuario";

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public Usuario registrarUsuario(@RequestBody Usuario usuario) {
        // El rol nunca lo decide el navegador al registrarse.
        usuario.setRol("user");
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<Usuario> iniciarSesion(@RequestBody Usuario usuario, HttpSession session) {
        Usuario usuarioAutenticado = usuarioService.iniciarSesion(
                usuario.getNombreUsuario(),
                usuario.getContrasena()
        );

        if (usuarioAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // La autenticación queda asociada a la HttpSession del navegador.
        session.setAttribute(USUARIO_ID, usuarioAutenticado.getId());
        session.setAttribute(USUARIO, usuarioAutenticado);

        return ResponseEntity.ok(usuarioAutenticado);
    }

    @GetMapping("/sesion")
    public ResponseEntity<Usuario> obtenerSesion(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute(USUARIO_ID);
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioService.obtenerPorId(usuarioId);
        if (usuario == null) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/cerrar-sesion")
    public ResponseEntity<Void> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosUsuarios(HttpSession session) {
        Usuario usuarioSesion = usuarioActual(session);

        if (usuarioSesion == null || !"admin".equalsIgnoreCase(usuarioSesion.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Usuario> usuarios = usuarioService.obtenerTodosUsuarios();
        usuarios.forEach(u -> u.setContrasena(null));
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, HttpSession session) {
        Usuario usuarioSesion = usuarioActual(session);

        if (usuarioSesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Un usuario solamente puede eliminar su propia cuenta.
        // La administración de usuarios queda fuera de este endpoint.
        if (!usuarioSesion.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        usuarioService.eliminarUsuario(id);
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, HttpSession session) {
        Usuario usuarioSesion = usuarioActual(session);

        if (usuarioSesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Nunca confiamos en un id enviado por el navegador para decidir
        // qué cuenta puede modificar el usuario.
        usuario.setId(usuarioSesion.getId());
        usuario.setRol(usuarioSesion.getRol());

        Usuario actualizado = usuarioService.actualizarUsuario(usuario);

        // Mantener la HttpSession sincronizada después de modificar el perfil.
        session.setAttribute(USUARIO_ID, actualizado.getId());
        session.setAttribute(USUARIO, actualizado);

        return ResponseEntity.ok(actualizado);
    }

    private Usuario usuarioActual(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute(USUARIO_ID);
        if (usuarioId == null) {
            return null;
        }
        return usuarioService.obtenerPorId(usuarioId);
    }
}
