package app.repositories;

import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.dto.filtros.FaltantesFiltro;
import app.dto.filtros.FiguritasFiltro;
import app.dto.filtros.RepetidasFiltro;
import app.repositories.impl.campos.CamposColeccion;

import java.util.List;

public interface RepositorioColecciones {

  Coleccion buscarPorId(String colId, CamposColeccion campos);

  void guardar(Coleccion coleccion);
  void guardar(Coleccion coleccion, CamposColeccion campos);

  void agregarFaltante(String colId, Figurita figId);
  void agregarRepetida(String colId, FiguritaIntercambiable figId);

  Repetidas<FiguritaIntercambiable> buscarRepetidas(String colId, RepetidasFiltro filtros, String colIdFaltantes);

  PaginaResultado<Figurita> buscarFaltantes(String colId, FaltantesFiltro filtros);

  PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesConFiltros(
      FiguritasFiltro filtros, int pagina, int tamanioPagina);

  /**
   * Busca por texto libre: cada término (separado por espacios) debe matchear
   * jugador, selección o número en OR. Entre términos se aplica AND.
   * El filtro {@code tipos} se aplica en AND sobre el resultado.
   */
  PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesPorQuery(
      String q, List<MetodoIntercambio> tipos, int pagina, int tamanioPagina);

  List<FiguritaIntercambiable> buscarIntercambiablesPorFiguritaIds(List<String> figuritaIds);
  List<FiguritaIntercambiable> buscarIntercambiablesPorPerfilId(String usuarioId);

  long contarRepetidas(List<MetodoIntercambio> filtros);
}
