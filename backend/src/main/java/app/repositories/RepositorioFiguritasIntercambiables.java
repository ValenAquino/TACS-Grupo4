package app.repositories;

import app.dto.PaginaResultado;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FiguritasFiltro;
import java.util.List;

public interface RepositorioFiguritasIntercambiables {

  /**
   * Filtra y pagina figuritas intercambiables según los criterios del filtro.
   * La implementación de base de datos hará esto en una sola query con índices.
   */
  PaginaResultado<FiguritaIntercambiable> buscarConFiltros(
      FiguritasFiltro filtros, int pagina, int tamanioPagina);

  /**
   * Busca por texto libre: cada término (separado por espacios) debe matchear
   * jugador, selección o número en OR. Entre términos se aplica AND.
   * El filtro {@code tipo} se aplica en AND sobre el resultado.
   */
  PaginaResultado<FiguritaIntercambiable> buscarPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina);

  List<FiguritaIntercambiable> buscarPorFiguritaIds(List<String> figuritaIds);
  List<FiguritaIntercambiable> buscarPorUsuarioId(String usuarioId);
  void guardar(FiguritaIntercambiable figuritaIntercambiable);
}
