package app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    String nombre,
    @NotBlank
    String contrasenia
) {
}
