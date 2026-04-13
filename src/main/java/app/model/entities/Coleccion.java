package app.model.entities;

import app.exceptions.FiguritaDuplicadaException;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class Coleccion {

  private String id;
  private List<Figurita> faltantes = new ArrayList<Figurita>();
  private List<FiguritaIntercambiable> repetidas = new ArrayList<FiguritaIntercambiable>();

  public Coleccion() {}

  public Coleccion(String id) {
    this.id = id;
  }

  public void agregarFaltante(Figurita faltante) {
    if(existeFaltante(faltante)) {
      throw new FiguritaDuplicadaException("Figurita ya listada como faltante");
    }

    this.faltantes.add(faltante);
  }

  public void agregarRepetida(FiguritaIntercambiable repetida) {

    for (FiguritaIntercambiable f : repetidas) {
      if (f.getFigurita().getId()
          .equals(repetida.getFigurita().getId())) {

        f.setCantidadDisponible(f.getCantidadDisponible() + repetida.getCantidadDisponible());
        return;
      }
    }

    repetidas.add(repetida);
  }

  private boolean existeFaltante(Figurita figurita) {
    return this.faltantes.contains(figurita);
  }
}
