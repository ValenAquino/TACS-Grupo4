package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class PerfilRequest {
  @JsonProperty("nombre")
  private String nombre;

  @JsonProperty("nombre_usuario")
  private String nombreUsuario;

  @JsonProperty("medios_de_contacto")
  private List<MedioDeContactoRequest> mediosDeContacto;
}
