
package com.aprendoiajugando.usuarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "El alias es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String alias;

    @NotNull(message = "El tipo de perfil es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfil tipoPerfil;

    // Temporalmente nullable para conservar usuarios antiguos de H2.
    @Column(length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public Usuario() {
    }

    public Usuario(String nombre, String telefono,
                   String alias, TipoPerfil tipoPerfil) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.alias = alias;
        this.tipoPerfil = tipoPerfil;
    }

    public Usuario(String nombre, String telefono,
                   String alias, TipoPerfil tipoPerfil, String password) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.alias = alias;
        this.tipoPerfil = tipoPerfil;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
