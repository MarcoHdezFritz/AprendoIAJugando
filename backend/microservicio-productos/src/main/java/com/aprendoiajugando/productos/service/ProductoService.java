
package com.aprendoiajugando.productos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.aprendoiajugando.productos.model.Producto;
import com.aprendoiajugando.productos.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cápsula no encontrada"
                ));
    }

    public Producto crearProducto(Producto producto) {
        producto.setId(null);
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto datos) {
        Producto producto = buscarPorId(id);

        producto.setTitulo(datos.getTitulo());
        producto.setTema(datos.getTema());
        producto.setDescripcion(datos.getDescripcion());
        producto.setCategoria(datos.getCategoria());
        producto.setUrlVideo(datos.getUrlVideo());
        producto.setActivo(datos.getActivo());

        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        Producto producto = buscarPorId(id);
        productoRepository.delete(producto);
    }
}
