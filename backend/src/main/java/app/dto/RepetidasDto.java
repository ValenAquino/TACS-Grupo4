package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RepetidasDto {
  private List<FiguritaIntercambiableDto> data;
  private int publicadas;
  private int disponibles;
  private int resultados;
  private int paginaActual;
  private int paginasTotales;
}
