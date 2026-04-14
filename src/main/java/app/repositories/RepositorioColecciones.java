package app.repositories;

import app.model.entities.Coleccion;

public interface RepositorioColecciones {

  public Coleccion buscarPorId(String colId);

  public void save(Coleccion coleccion);
}
