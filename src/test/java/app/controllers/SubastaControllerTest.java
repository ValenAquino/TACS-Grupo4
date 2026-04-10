package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SubastaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void crearSubastaNoFalla() throws Exception {
        mockMvc.perform(post("/subastas")).andExpect(status().isOk());
    }

    @Test
    void ofertarEnSubastaNoFalla() throws Exception {
        mockMvc.perform(post("/subastas/1/propuestas")).andExpect(status().isOk());
    }

}
