package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MedioDeContacto {
  private MedioComunicacion medioComunicacion;
  private String valor;
}
