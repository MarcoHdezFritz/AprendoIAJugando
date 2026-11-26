
package com.aprendoiajugando.lugares.controller;

import com.aprendoiajugando.lugares.model.Lugar;
import com.aprendoiajugando.lugares.service.LugarService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lugares")
@CrossOrigin(origins = "*")
public class LugarController {

    private final LugarService service;

    public LugarController(LugarService service) {
        this.service = service;
    }

    @GetMapping
    public List<Lugar> listar() {
        return service.listar();
    }

    @GetMapping("/activos")
    public List<Lugar> listarActivos() {
        return service.listarActivos();
    }

    @GetMapping("/{id}")
    public Lugar buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/qr/{codigo}")
    public Lugar buscarPorQr(@PathVariable String codigo) {
        return service.buscarPorQr(codigo);
    }

    @PostMapping
    public ResponseEntity<Lugar> crear(@Valid @RequestBody Lugar lugar) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(lugar));
    }

    @PutMapping("/{id}")
    public Lugar actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Lugar lugar) {
        return service.actualizar(id, lugar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
