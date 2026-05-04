package app.dto.subasta;

import java.util.List;
import lombok.Getter;

@Getter
public class SubastasParticipoResponseDto {
  private List<SubastaParticipoDto> activas;
  private List<SubastaParticipoDto> finalizadas;

  public SubastasParticipoResponseDto(List<SubastaParticipoDto> activas, List<SubastaParticipoDto> finalizadas) {
    this.activas = activas;
    this.finalizadas = finalizadas;
  }
}
