package app.dto.request;

import app.model.entities.MetodoIntercambio;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CalificacionRequest {
  @JsonProperty("valor")
  private Integer valor;

  @JsonProperty("destinatario_id")
  private String destinatarioId;

  @JsonProperty("descripcion")
  private String descripcion;

  @JsonProperty("transaction_id")
  private String transactionId;

  @JsonProperty("tipo_transaccion")
  private MetodoIntercambio tipoTransaccion;
}