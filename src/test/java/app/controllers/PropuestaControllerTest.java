package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PropuestaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void crearPropuestaDevuelve201() throws Exception {
        String json = """
        {
            "usuario_origen_id": "1000",
            "usuario_destino_id": "1001",
            "figurita_buscada_id": "ARG-10",
            "figuritas_ofrecidas_ids": ["FRA-10"]
        }
        """;

        mockMvc.perform(post("/propuestas")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated());
    }

    @Test
    void aceptarPropuestaNoFalla() throws Exception {
        mockMvc.perform(patch("/propuestas/2000/aceptar"))
                .andExpect(status().isOk());
    }

    @Test
    void rechazarPropuestaNoFalla() throws Exception {
        mockMvc.perform(patch("/propuestas/2000/rechazar"))
                .andExpect(status().isOk());
    }
}