package app.dto.request;

import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MedioDeContactoRequest {
  @JsonProperty("medio_comunicacion")
  @NotNull
  private MedioComunicacion medioComunicacion;

  @JsonProperty("valor")
  @NotBlank
  private String valor;

  public MedioDeContacto toEntity() {
    return new MedioDeContacto(medioComunicacion, valor);
  }
}
