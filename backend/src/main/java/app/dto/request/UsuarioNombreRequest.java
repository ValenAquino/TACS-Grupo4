package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UsuarioNombreRequest {
  @JsonProperty("nombre")
  @NotBlank
  private String nombre;
}
