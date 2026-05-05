package app.repositories.impl;

import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioFiguritas;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFiguritasEnMemoria implements RepositorioFiguritas {

  private final Map<String, Figurita> storage = new HashMap<>();

  @Override
  public Figurita buscarPorId(String id) {
    Figurita figurita = storage.values().stream()
        .filter(c -> c.getId().equals(id))
        .findFirst().orElse(null);

    if (figurita == null) {
      throw new NotFoundException("No se encontro la figurita");
    }

    return figurita;
  }

  @Override
  public List<Figurita> buscarConFiltros(FiguritasFiltro filtros) {
    List<Figurita> resultado = storage.values().stream()
        .filter(f -> filtros.numero() == null || filtros.numero() == f.getNumero())
        .filter(f -> filtros.seleccion() == null || f.getSeleccion().equals(filtros.seleccion()))
        .filter(f -> filtros.jugador() == null || f.getJugador().toLowerCase().contains(filtros.jugador().toLowerCase()))
        .toList();

    if (resultado.isEmpty()) {
      throw new NotFoundException("No se encontraron figuritas con esos filtros");
    }

    return resultado;
  }

  @Override
  public void guardar(Figurita figurita) {
    this.storage.put(figurita.getId(), figurita);
  }
}
