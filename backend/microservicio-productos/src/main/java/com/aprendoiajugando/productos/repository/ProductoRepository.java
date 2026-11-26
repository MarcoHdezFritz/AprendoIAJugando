
package com.aprendoiajugando.productos.repository;

import com.aprendoiajugando.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByTemaIgnoreCase(String tema);
}
