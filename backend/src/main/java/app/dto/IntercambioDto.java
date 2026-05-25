package app.dto;

import app.model.entities.EstadoProceso;
import app.model.entities.Propuesta;
import app.model.entities.Figurita;
import lombok.Getter;
import java.util.List;

@Getter
public class IntercambioDto {
  private String id;
  private PerfilDto destinatario;
  private Figurita figuritaBuscada;
  private List<Figurita> figuritasOfrecidas;
  private EstadoProceso estado;
  private boolean yaCalificado;

  public IntercambioDto(Propuesta propuesta, boolean yaCalificado) {
    this.id = propuesta.getId();
    this.destinatario = new PerfilDto(propuesta.getDestinatario());
    this.figuritaBuscada = propuesta.getFiguritaBuscada();
    this.figuritasOfrecidas = propuesta.getFiguritasOfrecidas();
    this.estado = propuesta.getEstadoActual().getValor();
    this.yaCalificado = yaCalificado;
  }
}