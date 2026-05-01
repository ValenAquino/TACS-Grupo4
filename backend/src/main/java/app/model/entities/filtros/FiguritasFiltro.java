package app.model.entities.filtros;

import app.model.entities.Seleccion;

public record FiguritasFiltro (
    String id,
    int numero,
    Seleccion seleccion,
    String jugador
) {
}
