package app.dto.filtros;

import app.model.entities.MetodoIntercambio;
import java.util.List;

public record FiguritasFiltro(
    String id,
    Integer numero,
    String seleccion,
    String jugador,
    List<MetodoIntercambio> tipos
) {}
