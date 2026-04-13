package app.repositories.impl;

import app.model.entities.Figurita;
import app.model.entities.Propuesta;
import app.repositories.RepositorioFiguritas;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RepositorioFiguritasEnMemoria implements RepositorioFiguritas {
  private final Map<String, Figurita> storage = new HashMap<>();

  @Override
  public Figurita findById(String id) {
    return storage.get(id);
  }

  @Override
  public void save(Figurita figurita) {
    storage.put(figurita.getId(), figurita);
  }
}
