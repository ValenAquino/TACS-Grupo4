package app.dto.propuesta;

import app.dto.CalificacionDto;
import app.dto.PropuestaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class PropuestasDto {
  private List<PropuestaDto> data;
  private int resultados;
  private int paginaActual;
  private int paginasTotales;
}
