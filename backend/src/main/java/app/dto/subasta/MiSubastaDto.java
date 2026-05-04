package app.dto.subasta;

import app.dto.FiguritaDto;
import app.model.entities.Subasta;
import app.model.entities.EstadoProceso;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MiSubastaDto {
  private String id;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaCierre;
  private FiguritaDto figuritaSubastada;
  private int cantidadOfertas;
  // activas
  private List<OfertaSubastaDto> ofertas;
  // finalizadas
  private String ganadorPerfilId;
  private String ganadorUsuario;
  private String ganadorLabel;

  public MiSubastaDto(Subasta subasta) {
    this.id = subasta.getId();
    this.fechaInicio = subasta.getFechaInicio();
    this.fechaCierre = subasta.getFechaCierre();
    this.figuritaSubastada = new FiguritaDto(subasta.getFiguritaSubastada());
    this.cantidadOfertas = subasta.getOfertas().size();

    if (subasta.estaActivo()) {
      this.ofertas = subasta.getOfertas().stream()
          .filter(p -> p.obtenerEstadoActual().getValor() != EstadoProceso.RECHAZADO)
          .map(OfertaSubastaDto::new)
          .toList();
    } else {
      subasta.getOfertas().stream()
          .filter(p -> p.obtenerEstadoActual().getValor() == EstadoProceso.ACEPTADO)
          .findFirst()
          .ifPresent(p -> {
            this.ganadorPerfilId = p.getAutor().getId();
            this.ganadorUsuario = p.getAutor().getNombre();
            this.ganadorLabel = p.getFiguritasOfrecidas().stream()
                .map(f -> f.getJugador() + " #" + f.getNumero())
                .reduce((a, b) -> a + " + " + b)
                .orElse("");
          });
    }
  }
}