package app.dto;

import app.model.entities.EstadoProceso;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class SubastaParticipoDto {
  private String id;
  private String autorNombre;
  private FiguritaDto figuritaSubastada;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaCierre;
  private String tuOfertaLabel;
  private EstadoProceso tuOfertaEstado;

  public SubastaParticipoDto(Subasta subasta, Propuesta tuOferta) {
    this.id = subasta.getId();
    this.autorNombre = subasta.getAutor().getNombre();
    this.figuritaSubastada = new FiguritaDto(subasta.getFiguritaSubastada());
    this.fechaInicio = subasta.getFechaInicio();
    this.fechaCierre = subasta.getFechaCierre();
    this.tuOfertaLabel = tuOferta.getFiguritasOfrecidas().stream()
        .map(f -> f.getJugador() + " #" + f.getNumero())
        .reduce((a, b) -> a + " + " + b)
        .orElse("");
    this.tuOfertaEstado = tuOferta.obtenerEstadoActual().getValor();
  }
}