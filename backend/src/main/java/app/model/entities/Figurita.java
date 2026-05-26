package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "figuritas")
public class Figurita {

  @Id
  private String id;

  private int numero;

  private String jugador;

  private Seleccion seleccion;

  private String posicion;

  private String imagenUrl;

  /** null = sin procesar, "EN_PROCESO", "COMPLETADO" */
  private String imagenStatus;

  private java.time.LocalDateTime imagenCreadoEn;
}
