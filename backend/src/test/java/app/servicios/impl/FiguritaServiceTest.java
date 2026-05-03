package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.dto.FiguritaIntercambiableDto;
import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
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

  @InjectMocks
  FiguritaService figuritaService;
  Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);

  @Test
  void buscarFiguritasPorSeleccionDevuelveResultados() {
    FiguritasFiltro filtros = new FiguritasFiltro(null, null, Seleccion.ARGENTINA, null);
    when(repositorioFiguritas.buscarConFiltros(filtros))
        .thenReturn(List.of(messi));

    List<Figurita> resultado =
        figuritaService.buscarFiguritas(filtros);

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getId());
  }

  @Test
  void buscarFiguritasSinResultadosLanzaNotFoundException() {
    when(repositorioFiguritas.buscarConFiltros(any()))
        .thenThrow(new NotFoundException("No se encontraron figuritas con esos filtros"));

    assertThrows(NotFoundException.class,
        () -> figuritaService.buscarFiguritas(any()));
  }
}
