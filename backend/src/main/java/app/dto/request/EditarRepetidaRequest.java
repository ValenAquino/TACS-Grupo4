package app.dto.request;

import app.model.entities.MetodoIntercambio;

import java.util.List;

public record EditarRepetidaRequest(
    Integer cantidadRepetidas,
    List<MetodoIntercambio> metodos
) {
}
