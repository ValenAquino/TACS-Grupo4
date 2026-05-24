package app.dto;

import app.model.entities.EstadoProceso;
import app.model.entities.Propuesta;
import app.model.entities.Figurita;
import lombok.Getter;
import java.util.List;

@Getter
public class PropuestaDto {
  private String id;
  private PerfilDto autor;
  private PerfilDto destinatario;
  private Figurita figuritaBuscada;
  private List<Figurita> figuritasOfrecidas;
  private EstadoProceso estado;

  public PropuestaDto(Propuesta propuesta) {
    this.id = propuesta.getId();
    this.autor = new PerfilDto(propuesta.getAutor());
    this.destinatario = new PerfilDto(propuesta.getDestinatario());
    this.figuritaBuscada = propuesta.getFiguritaBuscada();
    this.figuritasOfrecidas = propuesta.getFiguritasOfrecidas();
    this.estado = propuesta.getEstadoActual().getValor();
  }
}