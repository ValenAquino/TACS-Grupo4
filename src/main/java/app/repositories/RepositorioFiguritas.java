package app.repositories;

import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import java.util.List;

public interface RepositorioFiguritas {
  public Figurita findById(String id);
  public List<Figurita> buscarConFiltros(Integer numero, Seleccion seleccion, String jugador);

}
