package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CrearSubastaRequest {

  @JsonProperty("figurita_id")
  private String figuritaId;

  @JsonProperty("duracion_en_horas")
  private Integer duracionEnHoras;

  @JsonProperty("figuritas_deseadas_ids")
  private List<String> figuritasDeseadasIds = new ArrayList<>();

  @JsonProperty("calificacion_minima")
  private Integer calificacionMinima = 0;
}
