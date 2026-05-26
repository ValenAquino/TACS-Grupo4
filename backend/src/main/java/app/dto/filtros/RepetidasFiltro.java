package app.dto.filtros;

import app.model.entities.MetodoIntercambio;

public record RepetidasFiltro(
    MetodoIntercambio metodoIntercambio,
    Integer limite,
    Integer pagina
) {}
