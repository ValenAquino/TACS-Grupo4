package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PerfilRequest {
  @JsonProperty("nombre")
  private String nombre;
}
