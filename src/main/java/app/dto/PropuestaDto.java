package app.dto;

import app.model.entities.EstadoProceso;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PropuestaDto {
  private String id;
  private String autorId;
  private String destinatarioId;
  private String figuritaBuscadaId;
  private List<String> figuritasOfrecidasIds;
  private EstadoProceso estado;


}