
package com.aprendoiajugando.usuarios.repository;

import com.aprendoiajugando.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByAlias(String alias);

    boolean existsByAlias(String alias);
    Optional<Usuario> findFirstByAliasIgnoreCaseOrNombreIgnoreCase(
    String alias,
    String nombre
);
}