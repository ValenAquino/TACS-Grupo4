package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import app.MongoTestBase;
import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import java.util.List;
import app.servicios.ServicioFigurita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ServicioFiguritaTest extends MongoTestBase {

  @Autowired
  ServicioFigurita figuritaService;

  Figurita messi;
  Figurita mbappe;

  Coleccion intercambiable;
  Coleccion soloSubasta;

  @BeforeEach
  void setUp() {
    messi = Figurita.builder()
        .id("ARG-10")
        .numero(10)
        .jugador("Messi")
        .seleccion(Seleccion.ARGENTINA)
        .posicion("Delantero")
        .build();
    mbappe = Figurita.builder()
        .id("FRA-7")
        .numero(7)
        .jugador("Mbappé")
        .seleccion(Seleccion.FRANCIA)
        .posicion("Delantero")
        .build();

    repositorioFiguritas.guardar(messi);
    repositorioFiguritas.guardar(mbappe);

    intercambiable = new Coleccion("c-I");
    soloSubasta = new Coleccion("c-S");

    intercambiable.agregarRepetida(new FiguritaIntercambiable(
        messi, 2, List.of(MetodoIntercambio.INTERCAMBIO), "usuario-1"));

    soloSubasta.agregarRepetida(new FiguritaIntercambiable(
        mbappe, 1, List.of(MetodoIntercambio.SUBASTA), "usuario-2"));

    repositorioColecciones.guardar(intercambiable);
    repositorioColecciones.guardar(soloSubasta);
  }

  @Test
  void buscarFiguritas_sinFiltros_retornaTodasPaginadas() {

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 1, 12);

    assertEquals(2, resultado.cantidadDeElementos());
    assertEquals(2, resultado.contenido().size());
    assertEquals(1, resultado.cantidadDePaginas());
    assertEquals(1, resultado.numero());
  }

  @Test
  void buscarFiguritas_filtroTipoIntercambio_excluyeSoloSubasta() {

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, List.of(MetodoIntercambio.INTERCAMBIO), 1, 12);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals("ARG-10", resultado.contenido().get(0).getFiguritaId());
  }

  @Test
  void buscarFiguritas_paginacion_retornaSoloPaginaSolicitada() {
    Figurita f1 = Figurita.builder()
        .id("F-1")
        .numero(1)
        .jugador("Jugador1")
        .seleccion(Seleccion.ARGENTINA)
        .build();

    repositorioFiguritas.guardar(f1);

    FiguritaIntercambiable fi1 = new FiguritaIntercambiable(f1, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u1");

    Coleccion unaColeccionCualquiera = new Coleccion("c-C");

    unaColeccionCualquiera.agregarRepetida(fi1);

    repositorioColecciones.guardar(unaColeccionCualquiera);

    PaginaResultado<FiguritaIntercambiableDto> pagina0 =
        figuritaService.buscarFiguritas(null, null, null, null, 1, 2);
    PaginaResultado<FiguritaIntercambiableDto> pagina1 =
        figuritaService.buscarFiguritas(null, null, null, null, 2, 2);

    assertEquals(3, pagina0.cantidadDeElementos());
    assertEquals(2, pagina0.cantidadDePaginas());
    assertEquals(2, pagina0.contenido().size());
    assertEquals(1, pagina1.contenido().size());
  }

  @Test
  void buscarFiguritas_sinAgregar_retornaDosElementos() {

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 1, 12);

    assertEquals(2, resultado.cantidadDeElementos());
    assertEquals(1, resultado.cantidadDePaginas());
    assertTrue(!resultado.contenido().isEmpty());
  }

  @Test
  void buscarFiguritas_paginaFueraDeRango_retornaContenidoVacio() {

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 99, 12);

    assertEquals(2, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }
}
