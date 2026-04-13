package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class RepetidaRequest {

  @JsonProperty("cantidad_disponible")
  public Integer cantidadDisponible;

  @JsonProperty("numero_figurita")
  public String numeroFigurita;

  @JsonProperty("modo_intercambio")
  public List<String> modosIntercambio;
}
