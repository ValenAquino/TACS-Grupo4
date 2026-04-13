package app.model.entities;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Coleccion {

  private List<Figurita> faltantes = new ArrayList<Figurita>();
  private List<FiguritaIntercambiable> repetidas = new ArrayList<FiguritaIntercambiable>();

  public void agregarFaltante(Figurita faltante) {
    this.faltantes.add(faltante);
  }

}
