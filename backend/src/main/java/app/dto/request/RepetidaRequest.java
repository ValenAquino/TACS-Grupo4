package app.dto.request;

import app.model.entities.MetodoIntercambio;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public record RepetidaRequest (

  @JsonProperty("cantidad_existente")
  @Positive
  Integer cantidadExistente,

  @JsonProperty("fig_id")
  @NotBlank
  String figId,

  @JsonProperty("modos_intercambio")
  @NotEmpty
  List<MetodoIntercambio> modosIntercambio
) {}
