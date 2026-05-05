package app.dto.calificaciones;

import app.dto.CalificacionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class CalificacionesDto {
  private List<CalificacionDto> data;
  private int resultados;
  private int paginaActual;
  private int paginasTotales;
}
