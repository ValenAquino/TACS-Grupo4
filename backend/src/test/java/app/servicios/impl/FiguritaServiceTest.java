package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import app.exceptions.NotFoundException;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioFiguritasIntercambiables;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FiguritaServiceTest {

  @Mock
  RepositorioFiguritas repositorioFiguritas;
  @Mock
  RepositorioFiguritasIntercambiables repositorioIntercambiables;
  @InjectMocks
  FiguritaService figuritaService;

  Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
  Figurita mbappe = new Figurita("FRA-7", 7, "Mbappé", Seleccion.FRANCIA);

  FiguritaIntercambiable intercambiable = new FiguritaIntercambiable(
      messi, 2, List.of(MetodoIntercambio.INTERCAMBIO), "usuario-1");
  FiguritaIntercambiable subasta = new FiguritaIntercambiable(
      mbappe, 1, List.of(MetodoIntercambio.SUBASTA), "usuario-2");
  FiguritaIntercambiable ambos = new FiguritaIntercambiable(
      new Figurita("BRA-9", 9, "Neymar", Seleccion.BRASIL),
      3, List.of(MetodoIntercambio.SUBASTA_E_INTERCAMBIO), "usuario-3");

  @Test
  void buscarFiguritas_sinFiltros_retornaTodasPaginadas() {
    when(repositorioFiguritas.buscarConFiltros(null, null, null))
        .thenReturn(List.of(messi, mbappe));
    when(repositorioIntercambiables.buscarPorFiguritaIds(List.of("ARG-10", "FRA-7")))
        .thenReturn(List.of(intercambiable, subasta));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 0, 12);

    assertEquals(2, resultado.cantidadDeElementos());
    assertEquals(2, resultado.contenido().size());
    assertEquals(1, resultado.cantidadDePaginas());
    assertEquals(0, resultado.numero());
  }

  @Test
  void buscarFiguritas_filtroTipoIntercambio_noIncluyeSoloSubasta() {
    when(repositorioFiguritas.buscarConFiltros(null, null, null))
        .thenReturn(List.of(messi, mbappe));
    when(repositorioIntercambiables.buscarPorFiguritaIds(List.of("ARG-10", "FRA-7")))
        .thenReturn(List.of(intercambiable, subasta));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, MetodoIntercambio.INTERCAMBIO, 0, 12);

    assertEquals(1, resultado.cantidadDeElementos());
    assertEquals("ARG-10", resultado.contenido().get(0).getFiguritaId());
  }

  @Test
  void buscarFiguritas_filtroTipoIntercambio_incluyeSubastaEIntercambio() {
    Figurita bra = ambos.getFigurita();
    when(repositorioFiguritas.buscarConFiltros(null, null, null))
        .thenReturn(List.of(messi, bra));
    when(repositorioIntercambiables.buscarPorFiguritaIds(List.of("ARG-10", "BRA-9")))
        .thenReturn(List.of(intercambiable, ambos));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, MetodoIntercambio.INTERCAMBIO, 0, 12);

    assertEquals(2, resultado.cantidadDeElementos());
  }

  @Test
  void buscarFiguritas_paginacion_retornaSoloPaginaSolicitada() {
    Figurita f1 = new Figurita("F-1", 1, "Jugador1", Seleccion.ARGENTINA);
    Figurita f2 = new Figurita("F-2", 2, "Jugador2", Seleccion.ARGENTINA);
    Figurita f3 = new Figurita("F-3", 3, "Jugador3", Seleccion.ARGENTINA);
    FiguritaIntercambiable fi1 = new FiguritaIntercambiable(f1, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u1");
    FiguritaIntercambiable fi2 = new FiguritaIntercambiable(f2, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u2");
    FiguritaIntercambiable fi3 = new FiguritaIntercambiable(f3, 1, List.of(MetodoIntercambio.INTERCAMBIO), "u3");

    when(repositorioFiguritas.buscarConFiltros(null, null, null))
        .thenReturn(List.of(f1, f2, f3));
    when(repositorioIntercambiables.buscarPorFiguritaIds(List.of("F-1", "F-2", "F-3")))
        .thenReturn(List.of(fi1, fi2, fi3));

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
    when(repositorioFiguritas.buscarConFiltros(null, null, null))
        .thenThrow(new NotFoundException("No se encontraron figuritas con esos filtros"));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 0, 12);

    assertEquals(0, resultado.cantidadDeElementos());
    assertEquals(0, resultado.cantidadDePaginas());
    assertTrue(resultado.contenido().isEmpty());
  }

  @Test
  void buscarFiguritas_paginaFueraDeRango_retornaContenidoVacio() {
    when(repositorioFiguritas.buscarConFiltros(null, null, null))
        .thenReturn(List.of(messi));
    when(repositorioIntercambiables.buscarPorFiguritaIds(List.of("ARG-10")))
        .thenReturn(List.of(intercambiable));

    PaginaResultado<FiguritaIntercambiableDto> resultado =
        figuritaService.buscarFiguritas(null, null, null, null, 99, 12);

    assertEquals(1, resultado.cantidadDeElementos());
    assertTrue(resultado.contenido().isEmpty());
  }
}
