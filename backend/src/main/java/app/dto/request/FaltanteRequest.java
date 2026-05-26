package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FaltanteRequest {

  @JsonProperty("fig_id")
  @NotBlank
  private String figId;
}
