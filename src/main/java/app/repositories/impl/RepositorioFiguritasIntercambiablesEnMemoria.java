package app.repositories.impl;

import app.model.entities.FiguritaIntercambiable;
import app.repositories.RepositorioFiguritasIntercambiables;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RepositorioFiguritasIntercambiablesEnMemoria
    implements RepositorioFiguritasIntercambiables {

  private final Map<String, FiguritaIntercambiable> storage = new HashMap<>();

  @Override
  public List<FiguritaIntercambiable> buscarPorFiguritaIds(List<String> figuritaIds) {
    return storage.values().stream()
        .filter(fi -> figuritaIds.contains(fi.getFigurita().getId()))
        .toList();
  }

  @Override
  public void save(FiguritaIntercambiable figuritaIntercambiable) {
    storage.put(figuritaIntercambiable.getFigurita().getId(), figuritaIntercambiable);
  }
}