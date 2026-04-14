package app.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public class CrearPropuestaRequest {
  private String usuarioOrigenId;
  private String usuarioDestinoId;
  private String figuritaBuscadaId;
  private List<String> figuritasOfrecidasIds;
}