package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RepetidasDto {
  private List<FiguritaIntercambiableDto> data;
  private int publicadas;
  private int disponible;
  private int resultados;
  private int currentPage;
  private int totalPages;
}
