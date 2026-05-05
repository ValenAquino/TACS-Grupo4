package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PropuestasPorEstadoDto {
  private int pendientes;
  private int aceptadas;
  private int rechazadas;
}
