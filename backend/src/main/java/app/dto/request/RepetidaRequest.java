package app.dto.request;

import app.model.entities.MetodoIntercambio;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public record RepetidaRequest (

  @JsonProperty("cantidad_existente")
   Integer cantidadExistente,

  @JsonProperty("fig_id")
  String figId,

  @JsonProperty("modo_intercambio")
  MetodoIntercambio modoIntercambio
) {}
