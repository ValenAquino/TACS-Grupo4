package app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.servicios.ServicioColeccion;
import app.servicios.ServicioJwt;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControladorColeccionTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  private ServicioColeccion serviceColeccion;

  @MockBean
  private ServicioJwt servicioJwt;

  private final Cookie cookie = new Cookie("token", "fake-token");

  @BeforeEach
  void setup() {
    when(servicioJwt.getColeccionId("fake-token"))
        .thenReturn("1");
  }

  @Test
  void agregarRepetidaNoFalla() throws Exception {
    String json = """
            {
                "fig_id": "ARG-10",
                "cantidad_disponible": 2,
                "modos_intercambio": ["SUBASTA"]
            }
            """;

    mockMvc.perform(post("/colecciones/repetidas")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  void agregarFaltanteNoFalla() throws Exception {
    String json = """
            {
                "fig_id": "ARG-10"
            }
            """;

    mockMvc.perform(post("/colecciones/faltantes")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  void agregarFaltanteNoEncontradaDevuelve404() throws Exception {

    doThrow(new NotFoundException("No se encontro la figurita"))
        .when(serviceColeccion)
        .agregarFaltante("1", "ARG-10");

    String json = """
            {
                "fig_id": "ARG-10"
            }
            """;

    mockMvc.perform(post("/colecciones/faltantes")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isNotFound());
  }

  @Test
  void agregarRepetidaNoEncontradaDevuelve404() throws Exception {

    doThrow(new NotFoundException("No se encontro la figurita"))
        .when(serviceColeccion)
        .agregarRepetida(eq("1"), any(), any(), any());

    String json = """
            {
                "fig_id": "ARG-10",
                "cantidad_disponible": 2,
                "modos_intercambio": ["SUBASTA"]
            }
            """;

    mockMvc.perform(post("/colecciones/repetidas")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isNotFound());
  }

  @Test
  void agregarFaltanteDevuelve400SiDuplicada() throws Exception {

    doThrow(new BadRequestException("Figurita ya listada como faltante"))
        .when(serviceColeccion)
        .agregarFaltante(eq("1"), any());

    String json = """
            {
                "fig_id": "ARG-10"
            }
            """;

    mockMvc.perform(post("/colecciones/faltantes")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }
}