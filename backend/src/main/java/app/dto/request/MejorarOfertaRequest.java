package app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class MejorarOfertaRequest {
  @JsonProperty("figuritas_ofrecidas_id")
  private List<String> figuritasOfrecidasId;
}
