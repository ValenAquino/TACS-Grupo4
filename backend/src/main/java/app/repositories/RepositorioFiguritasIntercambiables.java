package app.repositories;

import app.dto.PaginaResultado;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import java.util.List;

public interface RepositorioFiguritasIntercambiables {

  /**
   * Filtra, ordena por número y pagina. La implementación de base de datos
   * hará esto en una sola query con índices.
   */
  PaginaResultado<FiguritaIntercambiable> buscarConFiltros(
      Integer numero, Seleccion seleccion, String jugador,
      MetodoIntercambio tipo, int pagina, int tamanioPagina);

  List<FiguritaIntercambiable> buscarPorFiguritaIds(List<String> figuritaIds);
  List<FiguritaIntercambiable> buscarPorUsuarioId(String usuarioId);
  void guardar(FiguritaIntercambiable figuritaIntercambiable);
}