package app.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.MongoTestBase;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.dto.filtros.FiguritasFiltro;
import org.junit.jupiter.api.Test;

public class RepositorioFiguritasTest extends MongoTestBase {

  @Test
  void buscarPorId_idValido_retornaFigurita() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    assertEquals("ARG-10", repositorioFiguritas.buscarPorId("ARG-10").getId());
  }

  @Test
  void buscarPorId_idInexistente_lanzaExcepcion() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    assertThrows(RuntimeException.class, () -> repositorioFiguritas.buscarPorId("INEXISTENTE"));
  }

  @Test
  void buscarConFiltros_sinFiltros_retornaTodo() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));
    repositorioFiguritas.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, null));

    var resultado = repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, null, null, null, null));

    assertEquals(2, resultado.size());
  }

  @Test
  void buscarConFiltros_porNumero_retornaCoincidencia() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));
    repositorioFiguritas.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, null));

    var resultado = repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, 10, null, null, null));

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porSeleccion_retornaCoincidencia() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));
    repositorioFiguritas.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, null));

    var resultado = repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, null, "ARGENTINA", null, null));

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porJugadorExacto_retornaCoincidencia() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    var resultado = repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, null, null, "Messi", null));

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_porJugadorParcial_retornaCoincidencia() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Lionel Messi", Seleccion.ARGENTINA, null));
    repositorioFiguritas.guardar(new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null));

    var resultado = repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, null, null, "messi", null));

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porJugadorCaseInsensitive_retornaCoincidencia() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    var resultado = repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, null, null, "MESSI", null));

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_sinResultados_daListaVacia() {
    repositorioFiguritas.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    assertEquals(0, repositorioFiguritas.buscarConFiltros(new FiguritasFiltro(null, 99, null, null, null)).size());
  }
}
