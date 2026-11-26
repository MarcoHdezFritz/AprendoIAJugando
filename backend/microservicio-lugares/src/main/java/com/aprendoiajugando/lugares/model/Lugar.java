
package com.aprendoiajugando.lugares.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lugares")
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String nombre;

    @NotBlank
    @Column(length = 1000)
    private String descripcion;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitud;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitud;

    private Double altitud;

    @NotBlank
    @Column(unique = true)
    private String qrCodigo;

    private Long productoId;

    private boolean activo = true;

    public Lugar() {
    }

    public Lugar(String nombre, String descripcion,
                 Double latitud, Double longitud,
                 Double altitud, String qrCodigo,
                 Long productoId, boolean activo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
        this.qrCodigo = qrCodigo;
        this.productoId = productoId;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getAltitud() {
        return altitud;
    }

    public void setAltitud(Double altitud) {
        this.altitud = altitud;
    }

    public String getQrCodigo() {
        return qrCodigo;
    }

    public void setQrCodigo(String qrCodigo) {
        this.qrCodigo = qrCodigo;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
