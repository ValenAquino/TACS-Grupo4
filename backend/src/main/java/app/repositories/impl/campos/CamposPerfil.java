package app.repositories.impl.campos;

import lombok.Getter;

@Getter
public class CamposPerfil {
  Boolean conMedioDeContacto;

  public CamposPerfil(Boolean conMedioDeContacto) {
    this.conMedioDeContacto = conMedioDeContacto;
  }
}
