package app.dto.request;

import app.model.entities.MetodoIntercambio;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EditarRepetidaRequest(
    @JsonProperty("cantidad_nueva")
    int cantidadNueva,
    List<MetodoIntercambio> metodos
) {
}
