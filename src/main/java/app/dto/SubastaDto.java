package app.dto;

import app.model.entities.Figurita;
import app.model.entities.Subasta;
import lombok.Getter;
import java.time.Duration;
import java.util.List;
@Getter
public class SubastaDto {
  PerfilDto perfilDto;
  long duracion;
  Figurita figurita;
  List<PropuestaDto> ofertas;

  public SubastaDto(Subasta subasta) {
    this.perfilDto = new PerfilDto(
        subasta.getAutor().getId(),
        subasta.getAutor().getNombre()
    );

    Duration duracion = Duration.between(
        subasta.getFechaInicio(),
        subasta.getFechaCierre()
    );

    this.duracion = duracion.toMinutes();
    this.figurita = subasta.getFiguritaSubastada();

    this.ofertas = subasta.getOfertas().stream()
        .map(p -> new PropuestaDto(
            p.getId(),
            p.getAutor().getId(),
            p.getDestinatario().getId(),
            p.getFiguritaBuscada().getId(),
            p.getFiguritasOfrecidas().stream()
                .map(Figurita::getId)
                .toList(),
            p.obtenerEstadoActual().getValor()
        ))
        .toList();
  }
}