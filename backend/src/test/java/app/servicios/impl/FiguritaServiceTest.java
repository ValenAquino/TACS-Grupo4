package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioPerfiles;
import java.util.List;

import app.servicios.ServicioFigurita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FiguritaServiceTest {

  @Mock
  RepositorioColecciones repositorioColecciones;
  @Mock RepositorioPerfiles repositorioPerfiles;
  @InjectMocks
  ServicioFigurita figuritaService;

  Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, "Delantero");
  Figurita mbappe = new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA, "Delantero");

  FiguritaIntercambiable intercambiable = new FiguritaIntercambiable(
      messi, 2, List.of(MetodoIntercambio.INTERCAMBIO), "usuario-1");
  FiguritaIntercambiable soloSubasta = new FiguritaIntercambiable(
      mbappe, 1, List.of(MetodoIntercambio.SUBASTA), "usuario-2");

  @Test
  void buscarFiguritas_sinFiltros_retornaTodasPaginadas() {
    when(repositorioColecciones.buscarIntercambiablesConFiltros(any(FiguritasFiltro.class), anyInt(), anyInt()))
        .thenReturn(new PaginaResultado<>(List.of(intercambiable, soloSubasta), 2, 1, 0));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 0, 12);

    assertEquals(2, resultado.cantidadDeElementos());
    assertEquals(2, resultado.contenido().size());
    assertEquals(1, resultado.cantidadDePaginas());
    assertEquals(0, resultado.numero());
  }

  @Test
  void buscarFiguritas_filtroTipoIntercambio_excluyeSoloSubasta() {
    when(repositorioColecciones.buscarIntercambiablesConFiltros(any(FiguritasFiltro.class), anyInt(), anyInt()))
        .thenReturn(new PaginaResultado<>(List.of(intercambiable), 1, 1, 0));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, MetodoIntercambio.INTERCAMBIO, 0, 12);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals("ARG-10", resultado.contenido().get(0).getFiguritaId());
  }

  @Test
  void buscarFiguritas_paginacion_retornaSoloPaginaSolicitada() {
    Figurita f1 = new Figurita("F-1", 1, "Jugador1", Seleccion.ARGENTINA, null);
    Figurita f2 = new Figurita("F-2", 2, "Jugador2", Seleccion.ARGENTINA, null);
    Figurita f3 = new Figurita("F-3", 3, "Jugador3", Seleccion.ARGENTINA, null);
    FiguritaIntercambiable fi1 = new FiguritaIntercambiable(f1, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u1");
    FiguritaIntercambiable fi2 = new FiguritaIntercambiable(f2, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u2");
    FiguritaIntercambiable fi3 = new FiguritaIntercambiable(f3, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u3");

    when(repositorioColecciones.buscarIntercambiablesConFiltros(any(FiguritasFiltro.class), anyInt(), anyInt()))
        .thenReturn(new PaginaResultado<>(List.of(fi1, fi2), 3, 2, 0))
        .thenReturn(new PaginaResultado<>(List.of(fi3), 3, 2, 1));

    PaginaResultado<FiguritaIntercambiableDto> pagina0 =
        figuritaService.buscarFiguritas(null, null, null, null, 0, 2);
    PaginaResultado<FiguritaIntercambiableDto> pagina1 =
        figuritaService.buscarFiguritas(null, null, null, null, 1, 2);

    assertEquals(3, pagina0.cantidadDeElementos());
    assertEquals(2, pagina0.cantidadDePaginas());
    assertEquals(2, pagina0.contenido().size());
    assertEquals(1, pagina1.contenido().size());
  }

  @Test
  void buscarFiguritas_sinResultados_retornaPaginaVacia() {
    when(repositorioColecciones.buscarIntercambiablesConFiltros(any(FiguritasFiltro.class), anyInt(), anyInt()))
        .thenReturn(new PaginaResultado<>(List.of(), 0, 0, 0));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 0, 12);

    assertEquals(0, resultado.cantidadDeElementos());
    assertEquals(0, resultado.cantidadDePaginas());
    assertTrue(resultado.contenido().isEmpty());
  }

  @Test
  void buscarFiguritas_paginaFueraDeRango_retornaContenidoVacio() {
    when(repositorioColecciones.buscarIntercambiablesConFiltros(any(FiguritasFiltro.class), anyInt(), anyInt()))
        .thenReturn(new PaginaResultado<>(List.of(), 1, 1, 99));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 99, 12);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }
}
