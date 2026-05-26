package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ContraseniaRequest {
  @JsonProperty("contrasenia_actual")
  @NotBlank
  private String contraseniaActual;

  @JsonProperty("contrasenia_nueva")
  @NotBlank
  private String contraseniaNueva;
}
