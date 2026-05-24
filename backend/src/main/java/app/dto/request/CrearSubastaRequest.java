package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class CrearSubastaRequest {

  private String figuritaId;

  private Integer duracionEnHoras;

  private List<String> figuritasDeseadasIds = new ArrayList<>();

  private Integer calificacionMinima = 0;
}
