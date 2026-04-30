package app.repositories.impl;

import app.dto.FiguritaIntercambiableDto;
import app.dto.RepetidasDto;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.RepetidasFiltro;
import app.repositories.RepositorioColecciones;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Repository
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

  public RepetidasDto buscarRepetidas(String colId, RepetidasFiltro filtros) {
    Coleccion col = this.storage.get(colId);

    List<FiguritaIntercambiable> repetidas = col.getRepetidas();

    int publicadas = repetidas.size();

    int disponibles = repetidas.stream()
        .mapToInt(FiguritaIntercambiable::getCantidadDisponible)
        .sum();

    if (Objects.equals(filtros.tipo(), "subasta")) {
      repetidas = repetidas.stream()
          .filter(fig -> fig.getMetodos().contains(MetodoIntercambio.SUBASTA)
              || fig.getMetodos().contains(MetodoIntercambio.SUBASTA_E_INTERCAMBIO))
          .toList();
    }

    if (Objects.equals(filtros.tipo(), "intercambio")) {
      repetidas = repetidas.stream()
          .filter(fig -> fig.getMetodos().contains(MetodoIntercambio.INTERCAMBIO)
              || fig.getMetodos().contains(MetodoIntercambio.SUBASTA_E_INTERCAMBIO))
          .toList();
    }

    int resultados = repetidas.size();

    int paginaActual = filtros.pagina();
    int limite = filtros.limite();

    int offset = (paginaActual - 1) * limite;

    List<FiguritaIntercambiableDto> repetidasMapeadas = repetidas.stream()
        .skip(offset)
        .limit(limite)
        .map(FiguritaIntercambiableDto::new)
        .toList();

    int paginasTotales = (resultados + filtros.limite() - 1) / filtros.limite();

    return new RepetidasDto(repetidasMapeadas, publicadas, disponibles, resultados, paginaActual, paginasTotales);
  }
}
