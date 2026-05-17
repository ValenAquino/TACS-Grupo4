package app.repositories;

import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.FiguritasFiltro;
import app.model.entities.filtros.RepetidasFiltro;

import java.util.List;

public interface RepositorioColecciones {

  Coleccion buscarPorId(String colId);

  void guardar(Coleccion coleccion);

  Repetidas<FiguritaIntercambiable> buscarRepetidas(String colId, RepetidasFiltro filtros);

  PaginaResultado<Figurita> buscarFaltantes(String colId, FaltantesFiltro filtros);

  PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesConFiltros(
      FiguritasFiltro filtros, int pagina, int tamanioPagina);

  /**
   * Busca por texto libre: cada término (separado por espacios) debe matchear
   * jugador, selección o número en OR. Entre términos se aplica AND.
   * El filtro {@code tipo} se aplica en AND sobre el resultado.
   */
  PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina);

  List<FiguritaIntercambiable> buscarIntercambiablesPorFiguritaIds(List<String> figuritaIds);
  List<FiguritaIntercambiable> buscarIntercambiablesPorPerfilId(String usuarioId);

}
