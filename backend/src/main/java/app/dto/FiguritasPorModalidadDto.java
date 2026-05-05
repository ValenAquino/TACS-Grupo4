package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FiguritasPorModalidadDto {
  private int soloIntercambio;
  private int soloSubasta;
  private int ambos;
}
