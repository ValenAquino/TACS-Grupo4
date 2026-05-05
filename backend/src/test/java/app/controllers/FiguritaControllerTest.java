package app.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.servicios.IFiguritaService;
import app.model.entities.filtros.FiguritasFiltro;
import app.servicios.impl.FiguritaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FiguritaControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean IFiguritaService figuritaService;

  Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
  FiguritaIntercambiable intercambiable = new FiguritaIntercambiable(
      messi, 2, List.of(MetodoIntercambio.INTERCAMBIO), "usuario-1");
  FiguritaIntercambiableDto dto = new FiguritaIntercambiableDto(intercambiable);

  @Test
  void obtenerFiguritas_conTodosLosFiltros_devuelve200() throws Exception {
    PaginaResultado<FiguritaIntercambiableDto> pagina =
        new PaginaResultado<>(List.of(dto), 1, 1, 0);

    when(figuritaService.buscarFiguritas(10, Seleccion.ARGENTINA, "Messi",
        MetodoIntercambio.INTERCAMBIO, 0, 12)).thenReturn(pagina);

    mockMvc.perform(get("/figuritas")
            .param("numero", "10")
            .param("seleccion", "ARGENTINA")
            .param("jugador", "Messi")
            .param("tipo", "INTERCAMBIO")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cantidad_de_elementos").value(1));
  }

  @Test
  void obtenerFiguritas_sinResultados_devuelve200ConListaVacia() throws Exception {
    PaginaResultado<FiguritaIntercambiableDto> paginaVacia =
        new PaginaResultado<>(List.of(), 0, 0, 0);

    when(figuritaService.buscarFiguritas(null, null, null, null, 0, 12))
        .thenReturn(paginaVacia);

    mockMvc.perform(get("/figuritas").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cantidad_de_elementos").value(0))
        .andExpect(jsonPath("$.contenido").isEmpty());
  }

  @Test
  void obtenerFiguritas_sizeMayorAlMaximo_acotaA40() throws Exception {
    when(figuritaService.buscarFiguritas(null, null, null, null, 0, 40))
        .thenReturn(new PaginaResultado<>(List.of(), 0, 0, 0));

    mockMvc.perform(get("/figuritas")
            .param("tamanioPagina", "100")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(figuritaService).buscarFiguritas(null, null, null, null, 0, 40);
  }

  @Test
  void obtenerFiguritas_tipoInvalido_devuelve400() throws Exception {
    mockMvc.perform(get("/figuritas")
            .param("tipo", "INVALIDO")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void obtenerFiguritas_conQ_usaBusquedaOrMultiTermino() throws Exception {
    PaginaResultado<FiguritaIntercambiableDto> pagina =
        new PaginaResultado<>(List.of(dto), 1, 1, 0);

    when(figuritaService.buscarPorQuery("messi argentina", null, 0, 12)).thenReturn(pagina);

    mockMvc.perform(get("/figuritas")
            .param("q", "messi argentina")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cantidad_de_elementos").value(1));

    verify(figuritaService).buscarPorQuery("messi argentina", null, 0, 12);
  }

  @Test
  void obtenerFiguritas_conQYTipo_combinaAmbos() throws Exception {
    when(figuritaService.buscarPorQuery("messi", MetodoIntercambio.SUBASTA, 0, 12))
        .thenReturn(new PaginaResultado<>(List.of(), 0, 0, 0));

    mockMvc.perform(get("/figuritas")
            .param("q", "messi")
            .param("tipo", "SUBASTA")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(figuritaService).buscarPorQuery("messi", MetodoIntercambio.SUBASTA, 0, 12);
  }
}
