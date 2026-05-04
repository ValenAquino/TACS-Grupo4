package app.dto;

import app.model.entities.Figurita;
import java.util.List;

public record FaltantesDto (
    List<Figurita> data,
    int resultados,
    int paginaActual,
    int paginasTotales
) {
}
