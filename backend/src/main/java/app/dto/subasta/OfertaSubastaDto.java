package app.dto.subasta;

import app.dto.PerfilDto;
import app.model.entities.Propuesta;
import app.model.entities.EstadoProceso;
import lombok.Getter;

@Getter
public class OfertaSubastaDto {
  private String id;
  private PerfilDto autor;
  //Todo: Ver como manejar esta informacion de propuesta
  private String label;
  private boolean seleccionada;

  public OfertaSubastaDto(Propuesta propuesta) {
    this.id = propuesta.getId();
    this.autor = new PerfilDto(propuesta.getAutor());
    this.label = propuesta.getFiguritasOfrecidas().stream()
        .map(f -> f.getJugador() + " #" + f.getNumero())
        .reduce((a, b) -> a + " + " + b)
        .orElse("");
    this.seleccionada = propuesta.getEstadoActual().getValor() == EstadoProceso.SELECCIONADO;
  }
}