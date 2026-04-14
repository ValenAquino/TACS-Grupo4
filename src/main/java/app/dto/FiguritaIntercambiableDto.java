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
  private String figuritaId;
  private Integer numero;
  private String jugador;
  private Seleccion seleccion;
  private Integer cantidadDisponible;
  private List<MetodoIntercambio> metodos;
  private String usuarioId;
}