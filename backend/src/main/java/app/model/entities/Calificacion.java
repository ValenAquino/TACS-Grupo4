package app.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Calificacion {
  private String id;
  @JsonIgnore
  private Perfil autor;
  //valor es un entero de 1 a 5
  private Integer valor;
  private String descripcion;
  //id subasta o id propuesta de intercambio
  private String transactionId;
  private MetodoIntercambio tipoTransaccion;
}
