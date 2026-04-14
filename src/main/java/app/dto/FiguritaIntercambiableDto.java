package app.dto;

import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class FiguritaIntercambiableDto {

  @JsonProperty("figurita_id")
  private String figuritaId;

  @JsonProperty("numero")
  private Integer numero;

  @JsonProperty("jugador")
  private String jugador;

  @JsonProperty("seleccion")
  private Seleccion seleccion;

  @JsonProperty("cantidad_disponible")
  private Integer cantidadDisponible;

  @JsonProperty("metodos")
  private List<MetodoIntercambio> metodos;

  @JsonProperty("usuario_id")
  private String usuarioId;
}