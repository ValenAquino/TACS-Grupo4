package app.repositories.impl;

import app.dto.FaltantesDto;
import app.dto.Repetidas;
import app.dto.RepetidasDto;
import app.model.entities.*;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RepositorioColeccionesEnMemoriaTest {

  private RepositorioColeccionesEnMemoria repositorio;

  Figurita messi;
  Figurita diMaria;
  Figurita dybala;

  @BeforeEach
  void setUp() {
    repositorio = new RepositorioColeccionesEnMemoria();
    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    diMaria = new Figurita("ARG-11", 11, "Di maria", Seleccion.ARGENTINA, null);
    dybala = new Figurita("ARG-21", 21, "Dybala", Seleccion.ARGENTINA, null);

  }

  @Test
  void findByIdNoEncuentraYtiraExcepcion() {
    Coleccion coleccion = new Coleccion("10");

    repositorio.guardar(coleccion);

    assertThrows(RuntimeException.class, () -> repositorio.buscarPorId("11"));
  }

  @Test
  void findByIdValido() {
    Coleccion coleccion = new Coleccion("10");

    repositorio.guardar(coleccion);

    assertEquals(coleccion.getId(), repositorio.buscarPorId("10").getId());
  }

  @Test
  void guardarSobrescribeColeccionConMismoId() {
    Coleccion original = new Coleccion("10");
    Coleccion nueva = new Coleccion("10");

    repositorio.guardar(original);
    repositorio.guardar(nueva);

    assertSame(nueva, repositorio.buscarPorId("10"));
  }

  @Test
  void buscarFaltantesDevuelveResultadosPaginados() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getFaltantes().addAll(List.of(
        messi,
        diMaria,
        dybala
    ));

    repositorio.guardar(coleccion);

    FaltantesDto dto = repositorio.buscarFaltantes(
        "10",
        new FaltantesFiltro(2, 1)
    );

    assertEquals(3, dto.resultados());
    assertEquals(1, dto.paginaActual());
    assertEquals(2, dto.paginasTotales());
    assertEquals(2, dto.data().size());
  }

  @Test
  void buscarFaltantesSegundaPagina() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getFaltantes().addAll(List.of(
        messi,
        diMaria,
        dybala
    ));

    repositorio.guardar(coleccion);

    FaltantesDto dto = repositorio.buscarFaltantes(
        "10",
        new FaltantesFiltro(2, 2)
    );

    assertEquals(1, dto.data().size());
    assertEquals(2, dto.paginaActual());
  }

  @Test
  void buscarRepetidasSinFiltroDevuelveTodo() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.SUBASTA)),
        new FiguritaIntercambiable(diMaria, 3, List.of(MetodoIntercambio.INTERCAMBIO)),
        new FiguritaIntercambiable(dybala, 1, List.of(MetodoIntercambio.SUBASTA))
    ));

    repositorio.guardar(coleccion);

    Repetidas dto = repositorio.buscarRepetidas(
        "10",
        new RepetidasFiltro(null, 10, 1)
    );

    assertEquals(3, dto.getPublicadas());
    assertEquals(6, dto.getDisponibles());
    assertEquals(3, dto.getData().cantidadDeElementos());
    assertEquals(3, dto.getData().contenido().size());
  }

  @Test
  void buscarRepetidasFiltraPorSubasta() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.SUBASTA)),
        new FiguritaIntercambiable(diMaria, 3, List.of(MetodoIntercambio.INTERCAMBIO)),
        new FiguritaIntercambiable(dybala, 1, List.of(MetodoIntercambio.SUBASTA))
    ));

    repositorio.guardar(coleccion);

    Repetidas dto = repositorio.buscarRepetidas(
        "10",
        new RepetidasFiltro(MetodoIntercambio.SUBASTA, 10, 1)
    );

    assertEquals(2, dto.getData());
    assertEquals(2, dto.getData().contenido().size());
  }

  @Test
  void buscarRepetidasFiltraPorIntercambio() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.SUBASTA)),
        new FiguritaIntercambiable(diMaria, 3, List.of(MetodoIntercambio.INTERCAMBIO)),
        new FiguritaIntercambiable(dybala, 1, List.of(MetodoIntercambio.SUBASTA))
    ));

    repositorio.guardar(coleccion);

    RepetidasDto dto = repositorio.buscarRepetidas(
        "10",
        new RepetidasFiltro(MetodoIntercambio.INTERCAMBIO, 10, 1)
    );

    assertEquals(1, dto.getResultados());
    assertEquals(1, dto.getData().size());
  }

  @Test
  void buscarRepetidasRespetaPaginacion() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.SUBASTA)),
        new FiguritaIntercambiable(diMaria, 3, List.of(MetodoIntercambio.INTERCAMBIO)),
        new FiguritaIntercambiable(dybala, 1, List.of(MetodoIntercambio.SUBASTA))
    ));

    repositorio.guardar(coleccion);

    RepetidasDto dto = repositorio.buscarRepetidas(
        "10",
        new RepetidasFiltro(null, 2, 2)
    );

    assertEquals(1, dto.getData().size());
    assertEquals(2, dto.getPaginaActual());
    assertEquals(2, dto.getPaginasTotales());
  }
}
