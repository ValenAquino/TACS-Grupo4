package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PerfilRequest {
  @JsonProperty("nombre")
  @NotBlank
  private String nombre;

  @JsonProperty("nombre_usuario")
  @NotBlank
  private String nombreUsuario;

  @JsonProperty("medios_de_contacto")
  private List<MedioDeContactoRequest> mediosDeContacto;
}
