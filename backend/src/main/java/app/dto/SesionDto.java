package app.dto;

public record SesionDto(
    String usuarioId,
    String rol,
    String perfilId,
    String colId
) {
}
