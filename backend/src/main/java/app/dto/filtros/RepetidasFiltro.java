package app.dto.filtros;

import app.model.entities.MetodoIntercambio;

public record RepetidasFiltro(
    MetodoIntercambio metodoIntercambio,
    // para coincidir las faltantes del perfilId con las repetidas del perfil actual
    String perfilId,
    Integer limite,
    Integer pagina
) {}
