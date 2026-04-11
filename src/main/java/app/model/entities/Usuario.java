package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Usuario {
  
  private Coleccion coleccion;
  private String telefono;
  private List<Integer> calificaciones;

}
