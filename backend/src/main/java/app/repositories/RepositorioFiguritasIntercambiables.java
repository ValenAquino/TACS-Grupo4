package app.repositories;

import app.dto.PaginaResultado;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import java.util.List;

public interface RepositorioFiguritasIntercambiables {

  PaginaResultado<FiguritaIntercambiable> buscarConFiltros(
      Integer numero, Seleccion seleccion, String jugador,
      MetodoIntercambio tipo, int pagina, int tamanioPagina);

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