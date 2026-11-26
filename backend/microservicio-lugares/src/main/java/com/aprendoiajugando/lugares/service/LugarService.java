
package com.aprendoiajugando.lugares.service;

import com.aprendoiajugando.lugares.model.Lugar;
import com.aprendoiajugando.lugares.repository.LugarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class LugarService {

    private final LugarRepository repository;

    public LugarService(LugarRepository repository) {
        this.repository = repository;
    }

    public List<Lugar> listar() {
        return repository.findAll();
    }

    public List<Lugar> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Lugar buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lugar no encontrado"));
    }

    public Lugar buscarPorQr(String qrCodigo) {
        return repository.findByQrCodigo(qrCodigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Código QR no encontrado"));
    }

    public Lugar crear(Lugar lugar) {
        return repository.save(lugar);
    }

    public Lugar actualizar(Long id, Lugar datos) {
        Lugar lugar = buscarPorId(id);

        lugar.setNombre(datos.getNombre());
        lugar.setDescripcion(datos.getDescripcion());
        lugar.setLatitud(datos.getLatitud());
        lugar.setLongitud(datos.getLongitud());
        lugar.setAltitud(datos.getAltitud());
        lugar.setQrCodigo(datos.getQrCodigo());
        lugar.setProductoId(datos.getProductoId());
        lugar.setActivo(datos.isActivo());

        return repository.save(lugar);
    }

    public void eliminar(Long id) {
        Lugar lugar = buscarPorId(id);
        repository.delete(lugar);
    }
}
