package app.repositories;

import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;

import java.util.List;

public interface RepositorioFiguritas {
  public Figurita buscarPorId(String id);
  public List<Figurita> buscarConFiltros(FiguritasFiltro filtros);
  public void guardar(Figurita figurita);

}
