package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
