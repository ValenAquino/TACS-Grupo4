package app.dto;

import app.model.entities.Figurita;
import app.model.entities.Subasta;
import lombok.Getter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
@Getter
public class SubastaDto {
  PerfilDto perfil;
  LocalDateTime inicio;
  LocalDateTime cierre;
  Number tiempoRestante;
  Figurita figurita;
  List<PropuestaDto> ofertas;

  public SubastaDto(Subasta subasta) {
    this.perfil = new PerfilDto(subasta.getAutor());
    this.inicio = subasta.getFechaInicio();
    this.cierre = subasta.getFechaCierre();

    Duration duracion = Duration.between(
        LocalDateTime.now(),
        subasta.getFechaCierre()
    );
    this.tiempoRestante = Math.max(0, duracion.toSeconds());
    this.figurita = subasta.getFiguritaSubastada();

    this.ofertas = subasta.getOfertas().stream()
        .map(PropuestaDto::new)
        .toList();
  }
}