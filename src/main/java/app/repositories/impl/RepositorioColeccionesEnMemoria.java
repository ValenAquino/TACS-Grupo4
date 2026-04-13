package app.repositories.impl;

import app.model.entities.Coleccion;
import app.repositories.RepositorioColecciones;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RepositorioColeccionesEnMemoria implements RepositorioColecciones {

  private final Map<String, Coleccion> storage = new HashMap<>();

  public Coleccion buscarPorId(String colId) {
    Coleccion col = this.storage.values()
        .stream()
        .filter(c -> c.getId().equals(colId))
        .findFirst().orElse(null);

    if(col == null) {
      //Tirar excepcion
    }

    return col;
  }

  public void save(Coleccion coleccion) {
    this.storage.put(coleccion.getId(), coleccion);
  }
}
