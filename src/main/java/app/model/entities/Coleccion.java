package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Coleccion {

  private List<Figurita> faltantes = new ArrayList<Figurita>();
  private List<FiguritaIntercambiable> repetidas = new ArrayList<FiguritaIntercambiable>();


}
