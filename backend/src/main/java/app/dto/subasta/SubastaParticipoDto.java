package app.dto.subasta;

import app.dto.FiguritaDto;
import app.dto.PerfilDto;
import app.model.entities.EstadoProceso;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class SubastaParticipoDto {
  private String id;
  private PerfilDto autor;
  private FiguritaDto figuritaSubastada;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaCierre;
  private String tuOfertaLabel;
  private EstadoProceso tuOfertaEstado;
  private boolean yaCalificado;

  public SubastaParticipoDto(Subasta subasta, Propuesta tuOferta) {
    this.id = subasta.getId();
    this.autor = new PerfilDto(subasta.getAutor());
    this.figuritaSubastada = new FiguritaDto(subasta.getFiguritaSubastada());
    this.fechaInicio = subasta.getFechaInicio();
    this.fechaCierre = subasta.getFechaCierre();
    this.tuOfertaLabel = tuOferta.getFiguritasOfrecidas().stream()
        .map(f -> f.getJugador() + " #" + f.getNumero())
        .reduce((a, b) -> a + " + " + b)
        .orElse("");
    this.tuOfertaEstado = tuOferta.obtenerEstadoActual().getValor();

    if (this.tuOfertaEstado == EstadoProceso.ACEPTADO) {
      this.yaCalificado = subasta.getAutor().getCalificaciones().stream()
          .anyMatch(c -> tuOferta.getAutor().getId().equals(c.getAutor().getId())
              && subasta.getId().equals(c.getTransactionId())
              && c.getTipoTransaccion() == MetodoIntercambio.SUBASTA);
    }
  }
}