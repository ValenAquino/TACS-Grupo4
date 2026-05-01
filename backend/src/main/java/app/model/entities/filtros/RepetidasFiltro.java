package app.model.entities.filtros;

import app.model.entities.MetodoIntercambio;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RepetidasFiltro(
    MetodoIntercambio metodoIntercambio,
    Integer limite,
    Integer pagina
) {}
