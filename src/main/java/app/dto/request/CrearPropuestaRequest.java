package app.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public class CrearPropuestaRequest {
  private String autorId;
  private String destinatarioId;
  private String figuritaBuscadaId;
  private List<String> figuritasOfrecidasIds;
}