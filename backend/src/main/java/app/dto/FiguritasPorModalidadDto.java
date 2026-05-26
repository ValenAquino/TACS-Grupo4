package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FiguritasPorModalidadDto {
  private long soloIntercambio;
  private long soloSubasta;
  private long ambos;
}
