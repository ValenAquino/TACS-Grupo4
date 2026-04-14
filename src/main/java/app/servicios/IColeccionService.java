package app.servicios;

import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import java.util.List;

public interface IColeccionService {
  public Figurita agregarFaltante(String colId, String figId);
  public FiguritaIntercambiable agregarRepetida(String colId, String userId, String figId, Integer
      cantidadDisponible, List<String> modosIntercambio);

}
