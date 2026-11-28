
package com.aprendoiajugando.usuarios.config;

import com.aprendoiajugando.usuarios.model.Usuario;
import com.aprendoiajugando.usuarios.model.TipoPerfil;
import com.aprendoiajugando.usuarios.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner crearUsuariosPredefinidos(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Crear Administrador
            if (!usuarioRepository.existsByAlias("Admin")) {

                Usuario admin = new Usuario(
                        "Administrador",
                        "000000000",
                        "Admin",
                        TipoPerfil.ADMINISTRADOR,
                        passwordEncoder.encode("123")
                );

                usuarioRepository.save(admin);

                System.out.println("Administrador creado correctamente.");

            } else {
                System.out.println("El administrador ya existe.");
            }

            // Crear Creador de Contenido
            if (!usuarioRepository.existsByAlias("GenCont")) {

                Usuario creador = new Usuario(
                        "Contenido",
                        "000000001",
                        "GenCont",
                        TipoPerfil.CREADOR_CONTENIDO,
                        passwordEncoder.encode("123")
                );

                usuarioRepository.save(creador);

                System.out.println("Creador de contenido creado correctamente.");

            } else {
                System.out.println("El creador de contenido ya existe.");
            }

            // Crear Cliente de prueba
            if (!usuarioRepository.existsByAlias("ClienteTest")) {

                Usuario cliente = new Usuario(
                        "Cliente de Prueba",
                        "000000002",
                        "ClienteTest",
                        TipoPerfil.CLIENTE,
                        passwordEncoder.encode("123")
                );

                usuarioRepository.save(cliente);

                System.out.println("Cliente de prueba creado correctamente.");

            } else {
                System.out.println("El cliente de prueba ya existe.");
            }
        };
    }
}
