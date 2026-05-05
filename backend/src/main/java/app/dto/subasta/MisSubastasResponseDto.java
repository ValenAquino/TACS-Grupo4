package app.dto.subasta;

import lombok.Getter;
import java.util.List;

@Getter
public class MisSubastasResponseDto {
  private List<MiSubastaDto> activas;
  private List<MiSubastaDto> finalizadas;

  public MisSubastasResponseDto(List<MiSubastaDto> activas, List<MiSubastaDto> finalizadas) {
    this.activas = activas;
    this.finalizadas = finalizadas;
  }
}