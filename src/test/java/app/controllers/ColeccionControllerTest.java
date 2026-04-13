package app.controllers;

import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.repositories.RepositorioColecciones;
import app.repositories.impl.RepositorioColeccionesEnMemoria;
import app.servicios.ColeccionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ColeccionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ColeccionService serviceColeccion;

    @Test
    void agregarRepetidaNoFalla() throws Exception {

        String json = """
        {
            "fig_id": 10,
            "cantidad_disponible": 2,
            "modos_intercambio": ["SUBASTA"]
        }
        """;

        mockMvc.perform(post("/coleccion/1/repetidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is(201));
    }

    @Test
    void agregarFaltanteNoFalla() throws Exception {
        Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);

        when(serviceColeccion.agregarFaltante("1", "ARG-10")).thenReturn(
            messi);

        String json = """
        {
            "fig_id": 10
        }
        """;

        mockMvc.perform(post("/coleccion/1/faltantes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().is(201));

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

        mockMvc.perform(post("/coleccion/1/faltantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is(404));
    }

    @Test
    void agregarRepetidaNoEncontradaDevuelve404() throws Exception {
        doThrow(new NotFoundException("No se encontro la figurita"))
            .when(serviceColeccion)
            .agregarRepetida(
                eq("1"),
                any(),     // figId
                any(),     // cantidad
                any()      // lista
            );

        String json = """
    {
        "fig_id": "ARG-10",
        "cantidad_disponible": 2,
        "modos_intercambio": ["SUBASTA"]
    }
    """;

        mockMvc.perform(post("/coleccion/1/repetidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }

    @Test
    void agregarFaltanteDevuelve400SiDuplicada() throws Exception {

        doThrow(new IllegalArgumentException("Figurita ya repetida"))
            .when(serviceColeccion)
            .agregarFaltante(
                eq("1"),
                any()
            );

        String json = """
            {
                "fig_id": "ARG-10
            }
            """;

        mockMvc.perform(post("/coleccion/1/repetidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is(400));
    }

}
