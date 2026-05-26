package app.dto.subasta;

import app.dto.FiguritaDto;
import app.dto.PerfilDto;
import app.dto.PropuestaDto;
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
  MiOfertaDto tuOferta;
  private boolean yaCalificado;

  public SubastaParticipoDto(Subasta subasta, Propuesta tuOferta, boolean yaCalificado) {
    this.id = subasta.getId();
    this.autor = new PerfilDto(subasta.getAutor());
    this.figuritaSubastada = new FiguritaDto(subasta.getFiguritaSubastada());
    this.fechaInicio = subasta.getFechaInicio();
    this.fechaCierre = subasta.getFechaCierre();
    this.tuOferta = new MiOfertaDto(tuOferta);
    this.yaCalificado = yaCalificado;
  }
}