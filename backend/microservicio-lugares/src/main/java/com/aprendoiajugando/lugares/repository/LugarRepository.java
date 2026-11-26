
package com.aprendoiajugando.lugares.repository;

import com.aprendoiajugando.lugares.model.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LugarRepository extends JpaRepository<Lugar, Long> {

    List<Lugar> findByActivoTrue();

    Optional<Lugar> findByQrCodigo(String qrCodigo);
}
