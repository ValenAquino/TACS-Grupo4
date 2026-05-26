package app.repositories.impl.campos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CamposSubasta {
  Boolean solicitadas;
  Boolean ofertadas;
  Boolean fechaCierre;

  public CamposSubasta(Boolean ofertadas, Boolean solicitadas) {
    this.ofertadas = ofertadas;
    this.solicitadas = solicitadas;
    this.fechaCierre = false;
  }
}
