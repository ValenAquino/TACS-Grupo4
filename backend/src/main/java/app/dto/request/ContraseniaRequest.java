package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ContraseniaRequest {
  @JsonProperty("contrasenia_actual")
  private String contraseniaActual;

  @JsonProperty("contrasenia_nueva")
  private String contraseniaNueva;
}
