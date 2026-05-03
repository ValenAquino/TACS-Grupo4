package app.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioFiguritasEnMemoriaTest {

  private RepositorioFiguritasEnMemoria repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioFiguritasEnMemoria();
  }

  @Test
  void findByIdNoEncuentraYtiraExcepcion() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);

    repositorio.guardar(messi);

    assertThrows(RuntimeException.class, () -> {
      repositorio.buscarPorId("11");
    });
  }

  @Test
  void findByIdValido() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);

    repositorio.guardar(messi);

    assertEquals(messi.getId(), repositorio.buscarPorId("ARG-10").getId());
  }

  @Test
  void buscarConFiltros_porNumero_retornaCoincidencia() {
    FiguritasFiltro filtros = new FiguritasFiltro(null, 10, Seleccion.ARGENTINA, null);

    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA));

    var resultado = repositorio.buscarConFiltros(filtros);

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarConFiltros_porSeleccion_retornaCoincidencia() {
    FiguritasFiltro filtros = new FiguritasFiltro(null, 10, Seleccion.ARGENTINA, null);

    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));
    repositorio.guardar(new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA));

    var resultado = repositorio.buscarConFiltros(filtros);

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_porJugador_retornaCoincidencia() {
    FiguritasFiltro filtros = new FiguritasFiltro(null, null, Seleccion.ARGENTINA, "messi");

    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));

    var resultado = repositorio.buscarConFiltros(filtros);

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarConFiltros_sinResultados_lanzaExcepcion() {
    FiguritasFiltro filtros = new FiguritasFiltro(null, 99, Seleccion.ARGENTINA, null);

    repositorio.guardar(new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA));

    assertThrows(RuntimeException.class,
        () -> repositorio.buscarConFiltros(filtros));
  }
}
