package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EditarOfertaRequest {
  @JsonProperty("figuritas_ofrecidas_id")
  @NotEmpty
  private List<String> figuritasOfrecidasId;
}
