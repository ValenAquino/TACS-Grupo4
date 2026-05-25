package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ContraseniaRequest {
  @NotBlank
  @JsonProperty("contrasenia_actual")
  private String contraseniaActual;

  @NotBlank
  @JsonProperty("contrasenia_nueva")
  private String contraseniaNueva;
}
