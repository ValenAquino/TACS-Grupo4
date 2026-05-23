package app.repositories.impl;

import app.MongoTestBase;
import app.dto.filtros.FiguritasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.model.entities.*;
import app.dto.filtros.FaltantesFiltro;
import app.dto.filtros.RepetidasFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RepositorioColeccionesTest extends MongoTestBase {

  Figurita messi;
  Figurita diMaria;
  Figurita dybala;

  @BeforeEach
  void setUp() {
    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    diMaria = new Figurita("ARG-11", 11, "Di maria", Seleccion.ARGENTINA, null);
    dybala = new Figurita("ARG-21", 21, "Dybala", Seleccion.ARGENTINA, null);

    repositorioFiguritas.guardar(messi);
    repositorioFiguritas.guardar(diMaria);
    repositorioFiguritas.guardar(dybala);
  }

  @Test
  void findByIdNoEncuentraYtiraExcepcion() {
    Coleccion coleccion = new Coleccion("10");

    repositorioColecciones.guardar(coleccion);

    assertThrows(RuntimeException.class, () -> repositorioColecciones.buscarPorId("11"));
  }

  @Test
  void findByIdValido() {
    Coleccion coleccion = new Coleccion("10");

    repositorioColecciones.guardar(coleccion);

    assertEquals(coleccion.getId(), repositorioColecciones.buscarPorId("10").getId());
  }

  @Test
  void buscarFaltantesDevuelveResultadosPaginados() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getFaltantes().addAll(List.of(
        messi,
        diMaria,
        dybala
    ));

    repositorioColecciones.guardar(coleccion);

    PaginaResultado<Figurita> dto = repositorioColecciones.buscarFaltantes(
        "10",
        new FaltantesFiltro(2, 1)
    );

    assertEquals(3, dto.cantidadDeElementos());
    assertEquals(1, dto.numero());
    assertEquals(2, dto.cantidadDePaginas());
    assertEquals(2, dto.contenido().size());
  }

  @Test
  void buscarFaltantesSegundaPagina() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getFaltantes().addAll(List.of(
        messi,
        diMaria,
        dybala
    ));

    repositorioColecciones.guardar(coleccion);

    PaginaResultado dto = repositorioColecciones.buscarFaltantes(
        "10",
        new FaltantesFiltro(2, 2)
    );

    assertEquals(1, dto.contenido().size());
    assertEquals(2, dto.numero());
  }

  @Test
  void buscarRepetidasSinFiltroDevuelveTodo() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        FiguritaIntercambiable.builder().figurita(messi).cantidadExistente(2).metodos(List.of(MetodoIntercambio.SUBASTA)).build(),
        FiguritaIntercambiable.builder().figurita(diMaria).cantidadExistente(3).metodos(List.of(MetodoIntercambio.INTERCAMBIO)).build(),
        FiguritaIntercambiable.builder().figurita(dybala).cantidadExistente(1).metodos(List.of(MetodoIntercambio.SUBASTA)).build()
    ));

    repositorioColecciones.guardar(coleccion);

    Repetidas dto = repositorioColecciones.buscarRepetidas(
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

    repositorioColecciones.guardar(coleccion);

    Repetidas dto = repositorioColecciones.buscarRepetidas(
        "10",
        new RepetidasFiltro(MetodoIntercambio.SUBASTA, 10, 1)
    );

    assertEquals(2, dto.getData().cantidadDeElementos());
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

    repositorioColecciones.guardar(coleccion);

    Repetidas<FiguritaIntercambiable> dto = repositorioColecciones.buscarRepetidas(
        "10",
        new RepetidasFiltro(MetodoIntercambio.INTERCAMBIO, 10, 1)
    );

    assertEquals(1, dto.getData().cantidadDeElementos());
    assertEquals(1, dto.getData().contenido().size());
  }

  @Test
  void buscarRepetidasRespetaPaginacion() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.SUBASTA)),
        new FiguritaIntercambiable(diMaria, 3, List.of(MetodoIntercambio.INTERCAMBIO)),
        new FiguritaIntercambiable(dybala, 1, List.of(MetodoIntercambio.SUBASTA))
    ));

    repositorioColecciones.guardar(coleccion);

    Repetidas dto = repositorioColecciones.buscarRepetidas(
        "10",
        new RepetidasFiltro(null, 2, 2)
    );

    assertEquals(1, dto.getData().contenido().size());
    assertEquals(2, dto.getData().numero());
    assertEquals(2, dto.getData().cantidadDePaginas());
  }

  @Test
  void buscarIntercambiablesConFiltrosPorTipo() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.getRepetidas().addAll(List.of(
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        ),
        new FiguritaIntercambiable(
            diMaria,
            1,
            List.of(MetodoIntercambio.INTERCAMBIO)
        )
    ));

    repositorioColecciones.guardar(coleccion);

    PaginaResultado<FiguritaIntercambiable> resultado =
        repositorioColecciones.buscarIntercambiablesConFiltros(
            new FiguritasFiltro(
                null,
                null,
                null,
                null,
                MetodoIntercambio.SUBASTA
            ),
            1,
            10
        );

    assertEquals(1, resultado.contenido().size());
    assertEquals("Messi",
        resultado.contenido().get(0).getFigurita().getJugador());
  }
}
