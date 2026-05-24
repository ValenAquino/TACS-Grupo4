package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UsuarioNombreRequest {
  @JsonProperty("nombre")
  private String nombre;
}
