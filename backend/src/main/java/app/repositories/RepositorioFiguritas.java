package app.repositories;

import app.model.entities.Figurita;
import app.dto.filtros.FiguritasFiltro;

import java.util.List;

public interface RepositorioFiguritas {

  /** @throws app.exceptions.NotFoundException si no existe figurita con ese id */
  Figurita buscarPorId(String id);


  List<Figurita> buscarPorIds(List<String> ids);
  /**
   * Retorna las figuritas que cumplan los filtros provistos.
   * Los parámetros nulos se ignoran. {@code jugador} usa contains case-insensitive.
   *
   * @throws app.exceptions.NotFoundException si ninguna figurita coincide con los filtros
   */
  List<Figurita> buscarConFiltros(FiguritasFiltro filtros);

  void guardar(Figurita figurita);
}
