package app.repositories.impl;

import app.model.entities.Figurita;
import app.repositories.RepositorioFiguritas;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFiguritasEnMemoria implements RepositorioFiguritas {

  public Figurita buscarPorId(String id) {
    return null;
  }
}
