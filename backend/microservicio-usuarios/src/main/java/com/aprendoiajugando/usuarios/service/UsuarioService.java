
package com.aprendoiajugando.usuarios.service;

import com.aprendoiajugando.usuarios.model.Usuario;
import com.aprendoiajugando.usuarios.repository.UsuarioRepository;
import com.aprendoiajugando.usuarios.dto.LoginRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Listar todos
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar por ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    // Crear usuario y cifrar contraseña
    public Usuario crearUsuario(Usuario usuario) {

        if (usuario.getPassword() == null ||
                usuario.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña es obligatoria"
            );
        }

        if (usuario.getPassword().length() > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña no puede superar los 72 caracteres"
            );
        }

        if (usuarioRepository.existsByAlias(usuario.getAlias())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alias ya está registrado"
            );
        }

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        return usuarioRepository.save(usuario);
    }

    // Actualizar datos y conservar contraseña si no se envía una nueva
    public Usuario actualizarUsuario(Long id, Usuario datos) {

        Usuario usuario = buscarPorId(id);

        if (!usuario.getAlias().equals(datos.getAlias()) &&
                usuarioRepository.existsByAlias(datos.getAlias())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alias ya está registrado"
            );
        }

        usuario.setNombre(datos.getNombre());
        usuario.setTelefono(datos.getTelefono());
        usuario.setAlias(datos.getAlias());
        usuario.setTipoPerfil(datos.getTipoPerfil());

        if (datos.getPassword() != null &&
                !datos.getPassword().isBlank()) {

            if (datos.getPassword().length() > 72) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La contraseña no puede superar los 72 caracteres"
                );
            }

            usuario.setPassword(
                    passwordEncoder.encode(datos.getPassword())
            );
        }

        return usuarioRepository.save(usuario);
    }

    // Iniciar sesión mediante correo/alias o nombre
    public Usuario iniciarSesion(LoginRequest request) {

        String identificador = request.alias().trim();

        Usuario usuario = usuarioRepository
                .findFirstByAliasIgnoreCaseOrNombreIgnoreCase(
                        identificador,
                        identificador
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Alias, nombre o contraseña incorrectos"
                ));

        if (usuario.getPassword() == null ||
                !passwordEncoder.matches(
                        request.password(),
                        usuario.getPassword()
                )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Alias, nombre o contraseña incorrectos"
            );
        }

        return usuario;
    }

    // Eliminar
    public void eliminarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}
