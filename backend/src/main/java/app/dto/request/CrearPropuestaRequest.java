package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public class CrearPropuestaRequest {

  @JsonProperty("destinatario_id")
  @NotBlank
  private String destinatarioId;

  @JsonProperty("figurita_buscada_id")
  @NotBlank
  private String figuritaBuscadaId;

  @JsonProperty("figuritas_ofrecidas_ids")
  @NotEmpty
  private List<String> figuritasOfrecidasIds;
}