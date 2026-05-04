package app.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioFiguritasEnMemoriaTest {

  private RepositorioFiguritasEnMemoria repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioFiguritasEnMemoria();
  }

  @Test
  void buscarPorId_idValido_retornaFigurita() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
    repositorio.guardar(messi);

    assertEquals("ARG-10", repositorio.buscarPorId("ARG-10").getId());
  }

  @Test
  void buscarPorId_idInexistente_lanzaExcepcion() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));

    assertThrows(RuntimeException.class, () -> repositorio.buscarPorId("INEXISTENTE"));
  }

  // --- buscarConFiltros: casos felices ---

  @Test
  void buscarConFiltros_sinFiltros_retornaTodo() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA));

    var resultado = repositorio.buscarConFiltros(null, null, null);

    assertEquals(2, resultado.size());
  }

  @Test
  void buscarConFiltros_porNumero_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA));

    var resultado = repositorio.buscarConFiltros(10, null, null);

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porSeleccion_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA));

    var resultado = repositorio.buscarConFiltros(null, Seleccion.ARGENTINA, null);

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porJugadorExacto_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));

    var resultado = repositorio.buscarConFiltros(null, null, "Messi");

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_porJugadorParcial_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Lionel Messi", Seleccion.ARGENTINA));
    repositorio.guardar(new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA));

    var resultado = repositorio.buscarConFiltros(null, null, "messi");

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porJugadorCaseInsensitive_retornaCoincidencia() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));

    var resultado = repositorio.buscarConFiltros(null, null, "MESSI");

    assertEquals(1, resultado.size());
  }

  // --- buscarConFiltros: casos tristes ---

  @Test
  void buscarConFiltros_sinResultados_lanzaNotFoundException() {
    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));

    assertThrows(RuntimeException.class, () -> repositorio.buscarConFiltros(99, null, null));
  }

  @Test
  void buscarConFiltros_repositorioVacio_lanzaNotFoundException() {
    assertThrows(RuntimeException.class, () -> repositorio.buscarConFiltros(null, null, null));
  }
}
