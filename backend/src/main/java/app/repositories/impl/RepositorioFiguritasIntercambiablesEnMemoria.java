package app.repositories.impl;

import app.dto.PaginaResultado;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.repositories.RepositorioFiguritasIntercambiables;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioFiguritasIntercambiablesEnMemoria
    implements RepositorioFiguritasIntercambiables {

  private final Map<String, FiguritaIntercambiable> storage = new HashMap<>();

  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarConFiltros(
      Integer numero, Seleccion seleccion, String jugador,
      MetodoIntercambio tipo, int pagina, int tamanioPagina) {

    List<FiguritaIntercambiable> filtradas = storage.values().stream()
        .filter(fi -> numero == null || fi.getFigurita().getNumero().equals(numero))
        .filter(fi -> seleccion == null || fi.getFigurita().getSeleccion().equals(seleccion))
        .filter(fi -> jugador == null || fi.getFigurita().getJugador().toLowerCase().contains(jugador.toLowerCase()))
        .filter(fi -> tipo == null || fi.soporta(tipo))
        .toList();

    int total = filtradas.size();
    int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / tamanioPagina);
    int fromIndex = Math.min(pagina * tamanioPagina, total);
    int toIndex = Math.min(fromIndex + tamanioPagina, total);

    return new PaginaResultado<>(filtradas.subList(fromIndex, toIndex), total, totalPages, pagina);
  }

  @Override
  public List<FiguritaIntercambiable> buscarPorFiguritaIds(List<String> figuritaIds) {
    return storage.values().stream()
        .filter(fi -> figuritaIds.contains(fi.getFigurita().getId()))
        .toList();
  }

  @Override
  public List<FiguritaIntercambiable> buscarPorUsuarioId(String perfilId) {
    return storage.values().stream()
        .filter(fi -> fi.getPerfilId().equals(perfilId))
        .toList();
  }

  @Override
  public void guardar(FiguritaIntercambiable figuritaIntercambiable) {
    storage.put(figuritaIntercambiable.getFigurita().getId(), figuritaIntercambiable);
  }
}
