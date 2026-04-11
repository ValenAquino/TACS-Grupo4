package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getOperacionesNoFalla() throws Exception {
        mockMvc.perform(get("/usuarios/1/operaciones")).andExpect(status().isOk());
    }

    @Test
    void calificarUsuarioNoFalla() throws Exception {
        mockMvc.perform(post("/usuarios/1/calificaciones")).andExpect(status().isOk());
    }

}
