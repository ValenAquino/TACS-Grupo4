package app.repositories;

import app.model.entities.FiguritaIntercambiable;
import java.util.List;

public interface RepositorioFiguritasIntercambiables {
  List<FiguritaIntercambiable> buscarPorFiguritaIds(List<String> figuritaIds);
  void save(FiguritaIntercambiable figuritaIntercambiable);
}