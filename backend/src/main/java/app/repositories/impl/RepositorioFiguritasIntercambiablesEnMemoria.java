package app.repositories.impl;

import app.dto.PaginaResultado;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioFiguritasIntercambiables;
import java.util.Arrays;
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
      FiguritasFiltro filtros, int pagina, int tamanioPagina) {

    List<FiguritaIntercambiable> filtradas = storage.values().stream()
        .filter(fi -> filtros.numero() == null || filtros.numero() == fi.getFigurita().getNumero())
        .filter(fi -> filtros.seleccion() == null || fi.getFigurita().getSeleccion().equals(filtros.seleccion()))
        .filter(fi -> filtros.jugador() == null || fi.getFigurita().getJugador().toLowerCase().contains(filtros.jugador().toLowerCase()))
        .filter(fi -> filtros.tipo() == null || fi.soporta(filtros.tipo()))
        .toList();

    return paginar(filtradas, pagina, tamanioPagina);
  }

  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina) {

    String[] terminos = q.trim().toLowerCase().split("\\s+");

    List<FiguritaIntercambiable> filtradas = storage.values().stream()
        .filter(fi -> tipo == null || fi.soporta(tipo))
        .filter(fi -> Arrays.stream(terminos).allMatch(t ->
            fi.getFigurita().getJugador().toLowerCase().contains(t) ||
            fi.getFigurita().getSeleccion().name().toLowerCase().contains(t) ||
            String.valueOf(fi.getFigurita().getNumero()).equals(t)
        ))
        .toList();

    return paginar(filtradas, pagina, tamanioPagina);
  }

  private PaginaResultado<FiguritaIntercambiable> paginar(
      List<FiguritaIntercambiable> lista, int pagina, int tamanioPagina) {
    int total = lista.size();
    int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / tamanioPagina);
    int fromIndex = Math.min(pagina * tamanioPagina, total);
    int toIndex = Math.min(fromIndex + tamanioPagina, total);
    return new PaginaResultado<>(lista.subList(fromIndex, toIndex), total, totalPages, pagina);
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
