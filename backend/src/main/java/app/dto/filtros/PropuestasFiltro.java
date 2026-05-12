package app.dto.filtros;

import app.model.entities.EstadoPropuesta;

public record PropuestasFiltro(
    String tipo,
    Integer pagina,
    Integer limite,
    EstadoPropuesta estado
) {
}
