package app.repositories.impl.campos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CamposSubasta {
  Boolean ofertas;
  Boolean figuritasSolicitadas;
  Boolean fechaCierre;

  public CamposSubasta(Boolean ofertas, Boolean figuritasSolicitadas) {
    this.ofertas = ofertas;
    this.figuritasSolicitadas = figuritasSolicitadas;
    this.fechaCierre = false;
  }
}
