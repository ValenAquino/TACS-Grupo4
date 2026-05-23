package app.dto.subasta;

import app.dto.FiguritaDto;
import app.model.entities.EstadoProceso;
import app.model.entities.Subasta;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MiSubastaFinalizadaDto {
  private String id;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaCierre;
  private FiguritaDto figuritaSubastada;
  private OfertaSubastaDto ofertaGanadora;
  private boolean yaCalificado;

  public MiSubastaFinalizadaDto(Subasta subasta, boolean yaCalificado) {
    this.id = subasta.getId();
    this.fechaInicio = subasta.getFechaInicio();
    this.fechaCierre = subasta.getFechaCierre();
    this.figuritaSubastada = new FiguritaDto(subasta.getFiguritaSubastada());
    this.ofertaGanadora = subasta.getOfertas().stream()
        .filter(p -> p.obtenerEstadoActual().getValor() == EstadoProceso.ACEPTADO)
        .findFirst()
        .map(OfertaSubastaDto::new)
        .orElse(null);
    this.yaCalificado = yaCalificado;
  }
}
