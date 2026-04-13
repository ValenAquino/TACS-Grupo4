package app.controllers;

import app.model.entities.Coleccion;
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

}
