package app.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.MongoTestBase;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioFiguritas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositorioFiguritasTest extends MongoTestBase {

  @Autowired
  private RepositorioFiguritas repositorio;

  @Test
  void buscarPorId_idValido_retornaFigurita() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    assertEquals("ARG-10", repositorio.buscarPorId("ARG-10").getId());
  }

  @Test
  void buscarPorId_idInexistente_lanzaExcepcion() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    assertThrows(RuntimeException.class, () -> repositorio.buscarPorId("INEXISTENTE"));
  }

  @Test
  void buscarConFiltros_sinFiltros_retornaTodo() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, null));

    var resultado = repositorio.buscarConFiltros(new FiguritasFiltro(null, null, null, null, null));

    assertEquals(2, resultado.size());
  }

  @Test
  void buscarConFiltros_porNumero_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, null));

    var resultado = repositorio.buscarConFiltros(new FiguritasFiltro(null, 10, null, null, null));

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porSeleccion_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, null));

    var resultado = repositorio.buscarConFiltros(new FiguritasFiltro(null, null, Seleccion.ARGENTINA, null, null));

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porJugadorExacto_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    var resultado = repositorio.buscarConFiltros(new FiguritasFiltro(null, null, null, "Messi", null));

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_porJugadorParcial_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Lionel Messi", Seleccion.ARGENTINA, null));
    repositorio.guardar(new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null));

    var resultado = repositorio.buscarConFiltros(new FiguritasFiltro(null, null, null, "messi", null));

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porJugadorCaseInsensitive_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    var resultado = repositorio.buscarConFiltros(new FiguritasFiltro(null, null, null, "MESSI", null));

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_sinResultados_daListaVacia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null));

    assertEquals(0, repositorio.buscarConFiltros(new FiguritasFiltro(null, 99, null, null, null)).size());
  }
}
