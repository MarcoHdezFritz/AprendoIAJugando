
package com.aprendoiajugando.usuarios.controller;

import com.aprendoiajugando.usuarios.model.Usuario;
import com.aprendoiajugando.usuarios.service.UsuarioService;
import com.aprendoiajugando.usuarios.dto.LoginRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(
            @Valid @RequestBody Usuario usuario) {

        Usuario nuevo = usuarioService.crearUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {

        return ResponseEntity.ok(
                usuarioService.actualizarUsuario(id, usuario)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> iniciarSesion(
            @Valid @RequestBody LoginRequest request) {

        Usuario usuario = usuarioService.iniciarSesion(request);

        return ResponseEntity.ok(usuario);
    }
}
