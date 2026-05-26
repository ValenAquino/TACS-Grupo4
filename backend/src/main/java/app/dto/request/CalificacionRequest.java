package app.dto.request;

import app.model.entities.MetodoIntercambio;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CalificacionRequest {
  @JsonProperty("valor")
  @Min(1)
  @Max(5)
  @NotNull
  private Integer valor;

  @JsonProperty("destinatario_id")
  @NotBlank
  private String destinatarioId;

  @JsonProperty("descripcion")
  private String descripcion;

  @JsonProperty("transaction_id")
  @NotBlank
  private String transactionId;

  @JsonProperty("tipo_transaccion")
  @NotNull
  private MetodoIntercambio tipoTransaccion;
}