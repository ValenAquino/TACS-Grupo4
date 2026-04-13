package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ColeccionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void agregarRepetidaNoFalla() throws Exception {
        String json = """
        {
            "numeroFigurita": 10,
            "cantidadDisponible": 2,
            "modosIntercambio": ["SUBASTA"]
        }
        """;

        mockMvc.perform(post("/coleccion/1/repetidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated());
    }

    @Test
    void agregarFaltanteNoFalla() throws Exception {
        mockMvc.perform(post("/coleccion/1/faltantes")).andExpect(status().isOk());
    }

}
