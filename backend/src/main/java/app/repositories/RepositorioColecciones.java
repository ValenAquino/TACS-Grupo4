package app.repositories;

import app.dto.RepetidasDto;
import app.model.entities.Coleccion;
import app.model.entities.filtros.RepetidasFiltro;

public interface RepositorioColecciones {

  public Coleccion buscarPorId(String colId);

  public void guardar(Coleccion coleccion);

  public RepetidasDto buscarRepetidas(String colId, RepetidasFiltro filtros);
}
