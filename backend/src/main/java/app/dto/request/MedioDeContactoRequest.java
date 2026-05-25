package app.dto.request;

import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MedioDeContactoRequest {
  @JsonProperty("medio_comunicacion")
  private MedioComunicacion medioComunicacion;

  @JsonProperty("valor")
  private String valor;

  public MedioDeContacto toEntity() {
    return new MedioDeContacto(medioComunicacion, valor);
  }
}
