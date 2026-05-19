package app.dto.filtros;

import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;

public record FiguritasFiltro(
    String id,
    Integer numero,
    Seleccion seleccion,
    String jugador,
    MetodoIntercambio tipo
) {}
