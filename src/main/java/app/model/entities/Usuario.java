package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Usuario {
  
  public Coleccion coleccion;
  public String telefono;
  public List<Integer> calificaciones;

}
