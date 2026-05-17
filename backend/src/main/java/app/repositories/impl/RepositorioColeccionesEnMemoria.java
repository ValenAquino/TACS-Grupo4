package app.repositories.impl;

import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.FiguritasFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import app.repositories.RepositorioColecciones;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RepositorioColeccionesEnMemoria implements RepositorioColecciones {

  private final Map<String, Coleccion> storage = new HashMap<>();

  public Coleccion buscarPorId(String colId) {
    Coleccion col = this.storage.values()
        .stream()
        .filter(c -> c.getId().equals(colId))
        .findFirst().orElse(null);

    if(col == null) {
      throw new NotFoundException("No se encontro la coleccion");
    }

    return col;
  }

  public void guardar(Coleccion coleccion) {
    this.storage.put(coleccion.getId(), coleccion);
  }

  public Repetidas<FiguritaIntercambiable> buscarRepetidas(String colId, RepetidasFiltro filtros) {
    Coleccion col = this.storage.get(colId);

    List<FiguritaIntercambiable> repetidas = col.getRepetidas();

    int publicadas = repetidas.size();

    int disponibles = repetidas.stream()
        .mapToInt(FiguritaIntercambiable::getCantidadDisponible)
        .sum();

    if (Objects.equals(filtros.metodoIntercambio(), MetodoIntercambio.SUBASTA)) {
      repetidas = repetidas.stream()
          .filter(fig -> fig.getMetodos().contains(MetodoIntercambio.SUBASTA))
          .toList();
    }

    if (Objects.equals(filtros.metodoIntercambio(), MetodoIntercambio.INTERCAMBIO)) {
      repetidas = repetidas.stream()
          .filter(fig -> fig.getMetodos().contains(MetodoIntercambio.INTERCAMBIO))
          .toList();
    }

    int resultados = repetidas.size();

    int paginaActual = filtros.pagina();
    int limite = filtros.limite();

    int offset = (paginaActual - 1) * limite;

    List<FiguritaIntercambiable> repetidasMapeadas = repetidas.stream()
        .skip(offset)
        .limit(limite)
        .toList();

    int paginasTotales = (resultados + filtros.limite() - 1) / filtros.limite();

    PaginaResultado<FiguritaIntercambiable> data = new PaginaResultado<>(repetidasMapeadas, resultados, paginaActual, paginasTotales);

    return new Repetidas<>(publicadas, disponibles, data);
  }

  public PaginaResultado<Figurita> buscarFaltantes(String colId, FaltantesFiltro filtros) {
    Coleccion col = this.storage.get(colId);

    List<Figurita> faltantes = col.getFaltantes();

    int resultados = faltantes.size();

    int paginaActual = filtros.pagina();
    int limite = filtros.limite();
    int offset = (paginaActual - 1) * limite;

    faltantes = faltantes.stream()
        .skip(offset)
        .limit(limite)
        .toList();

    int paginasTotales = (resultados + filtros.limite() - 1) / filtros.limite();

    return null;
  }
  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesConFiltros(
      FiguritasFiltro filtros, int pagina, int tamanioPagina) {
    return null;
  }
  @Override
  public PaginaResultado<FiguritaIntercambiable> buscarIntercambiablesPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina) {
    return null; //??
  }

  @Override
  public List<FiguritaIntercambiable> buscarIntercambiablesPorFiguritaIds(List<String> figuritaIds) {
    return null;
  }

  @Override
  public List<FiguritaIntercambiable> buscarIntercambiablesPorPerfilId(String perfilId) {
    return null;
  }
}
