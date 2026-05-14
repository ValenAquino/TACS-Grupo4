package app.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Document(collection = "calificaciones")
public class Calificacion {
  @Id
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
