package app.dto.subasta;

import app.dto.FiguritaDto;
import app.dto.PerfilDto;
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
  private PerfilDto ganador;
  private String ganadorLabel;
  private boolean yaCalificado;

  public MiSubastaDto(Subasta subasta, boolean yaCalificado) {
    this.id = subasta.getId();
    this.fechaInicio = subasta.getFechaInicio();
    this.fechaCierre = subasta.getFechaCierre();
    this.figuritaSubastada = new FiguritaDto(subasta.getFiguritaSubastada());
    this.cantidadOfertas = subasta.getOfertas().size();

    this.ofertas = subasta.getOfertas().stream().map(OfertaSubastaDto::new).toList();

    if (!subasta.estaActivo()) {
      subasta.getOfertas().stream()
          .filter(p -> p.obtenerEstadoActual().getValor() == EstadoProceso.ACEPTADO)
          .findFirst()
          .ifPresent(p -> {
            this.ganador = new PerfilDto(p.getAutor());
            this.ganadorLabel = p.getFiguritasOfrecidas().stream()
                .map(f -> f.getJugador() + " #" + f.getNumero())
                .reduce((a, b) -> a + " + " + b)
                .orElse("");
            this.yaCalificado = yaCalificado;
//            this.yaCalificado = p.getAutor().getCalificaciones().stream()
//                .anyMatch(c -> subasta.getAutor().getId().equals(c.getAutor().getId())
//                    && subasta.getId().equals(c.getTransactionId())
//                    && c.getTipoTransaccion() == MetodoIntercambio.SUBASTA);
          });
    }
  }
}