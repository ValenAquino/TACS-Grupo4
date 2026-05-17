package app.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
import java.util.List;

import app.repositories.RepositorioColecciones;
import app.repositories.implMongo.RepositorioColeccionMongo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioFiguritasIntercambiablesEnMemoriaTest {

  private RepositorioColecciones repositorio;
  private FiguritaIntercambiable intercambiableMessi;
  private FiguritaIntercambiable intercambiableMbappe;
  private Coleccion coleccion;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioColeccionMongo();
    coleccion = new Coleccion("c-1");
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, "Delantero");
    Figurita mbappe = new Figurita("FRA-10", 10, "Mbappé", Seleccion.FRANCIA, "Delantero");
    intercambiableMessi = new FiguritaIntercambiable(messi, 3, List.of(MetodoIntercambio.INTERCAMBIO), "perfil-1");
    intercambiableMbappe = new FiguritaIntercambiable(mbappe, 2, List.of(MetodoIntercambio.SUBASTA), "perfil-2");
    coleccion.agregarRepetida(intercambiableMessi);
    coleccion.agregarRepetida(intercambiableMbappe);
    repositorio.guardar(coleccion);
  }

  // buscarConFiltros

  @Test
  void buscarConFiltros_sinFiltros_retornaTodas() {
    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, null), 0, 10);

    assertEquals(2, resultado.cantidadDeElementos());
  }

  @Test
  void buscarConFiltros_porNumero_retornaCoincidencia() {
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, "Extremo");
    coleccion.agregarRepetida(new FiguritaIntercambiable(diMaria, 1, List.of(MetodoIntercambio.INTERCAMBIO), "perfil-1"));
    repositorio.guardar(coleccion);

    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, 11, null, null, null), 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals("ARG-11", resultado.contenido().get(0).getFigurita().getId());
  }

  @Test
  void buscarConFiltros_porSeleccion_retornaCoincidencia() {
    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, Seleccion.ARGENTINA, null, null), 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals(Seleccion.ARGENTINA, resultado.contenido().get(0).getFigurita().getSeleccion());
  }

  @Test
  void buscarConFiltros_porJugadorParcialCaseInsensitive_retornaCoincidencia() {
    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, "MESSI", null), 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
  }

  @Test
  void buscarConFiltros_porTipoIntercambio_retornaCoincidencia() {
    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, MetodoIntercambio.INTERCAMBIO), 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().get(0).soporta(MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void buscarConFiltros_porTipoSubasta_retornaCoincidencia() {
    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, MetodoIntercambio.SUBASTA), 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().get(0).soporta(MetodoIntercambio.SUBASTA));
  }

  @Test
  void buscarConFiltros_sinResultados_retornaPaginaVacia() {
    var resultado = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, 99, null, null, null), 0, 10);

    assertEquals(0, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }

  @Test
  void buscarConFiltros_conPaginacion_retornaPaginaCorrecta() {
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, "Extremo");
    coleccion.agregarRepetida(new FiguritaIntercambiable(diMaria, 1, List.of(MetodoIntercambio.INTERCAMBIO), "perfil-1"));
    repositorio.guardar(coleccion);

    var pagina0 = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, null), 0, 2);
    var pagina1 = repositorio.buscarIntercambiablesConFiltros(new FiguritasFiltro(null, null, null, null, null), 1, 2);

    assertEquals(3, pagina0.cantidadDeElementos());
    assertEquals(2, pagina0.contenido().size());
    assertEquals(1, pagina1.contenido().size());
  }

  // buscarPorQuery

  @Test
  void buscarPorQuery_porJugador_retornaCoincidencia() {
    var resultado = repositorio.buscarIntercambiablesPorQuery("messi", null, 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
  }

  @Test
  void buscarPorQuery_porSeleccion_retornaCoincidencia() {
    var resultado = repositorio.buscarIntercambiablesPorQuery("argentina", null, 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals(Seleccion.ARGENTINA, resultado.contenido().get(0).getFigurita().getSeleccion());
  }

  @Test
  void buscarPorQuery_porNumero_retornaAmbas() {
    var resultado = repositorio.buscarIntercambiablesPorQuery("10", null, 0, 10);

    assertEquals(2, resultado.cantidadDeElementos());
  }

  @Test
  void buscarPorQuery_variosTerminos_aplicaAnd() {
    var resultado = repositorio.buscarIntercambiablesPorQuery("10 argentina", null, 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals(Seleccion.ARGENTINA, resultado.contenido().get(0).getFigurita().getSeleccion());
  }

  @Test
  void buscarPorQuery_conFiltroTipo_aplicaAnd() {
    var resultado = repositorio.buscarIntercambiablesPorQuery("10", MetodoIntercambio.INTERCAMBIO, 0, 10);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().get(0).soporta(MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void buscarPorQuery_sinResultados_retornaPaginaVacia() {
    var resultado = repositorio.buscarIntercambiablesPorQuery("inexistente", null, 0, 10);

    assertEquals(0, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }

  // buscarPorFiguritaIds

  @Test
  void buscarPorFiguritaIds_idsExistentes_retornaLista() {
    var resultado = repositorio.buscarIntercambiablesPorFiguritaIds(List.of("ARG-10", "FRA-10"));

    assertEquals(2, resultado.size());
  }

  @Test
  void buscarPorFiguritaIds_idsInexistentes_retornaVacio() {
    var resultado = repositorio.buscarIntercambiablesPorFiguritaIds(List.of("INEXISTENTE"));

    assertTrue(resultado.isEmpty());
  }

  // buscarPorUsuarioId

  @Test
  void buscarPorUsuarioId_perfilIdExistente_retornaFiguritas() {
    var resultado = repositorio.buscarIntercambiablesPorUsuarioId("perfil-1");

    assertEquals(1, resultado.size());
  }

  @Test
  void buscarPorUsuarioId_perfilIdInexistente_retornaVacio() {
    var resultado = repositorio.buscarIntercambiablesPorUsuarioId("perfil-99");

    assertTrue(resultado.isEmpty());
  }
}
