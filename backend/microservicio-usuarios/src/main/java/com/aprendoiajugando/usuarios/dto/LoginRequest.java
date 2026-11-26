
package com.aprendoiajugando.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El alias es obligatorio")
        String alias,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
