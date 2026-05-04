package app.dto;

import java.util.List;

public record SugerenciaPaginadaDto(
    List<SugerenciaDto> data,
    int resultados,
    int paginaActual,
    int paginasTotales
) {
}
