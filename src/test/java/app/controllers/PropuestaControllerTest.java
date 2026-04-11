package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PropuestaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void crearPropuestaNoFalla() throws Exception {
        mockMvc.perform(post("/propuestas")).andExpect(status().isOk());
    }

    @Test
    void responderPropuestaNoFalla() throws Exception {
        mockMvc.perform(patch("/propuestas/1")).andExpect(status().isOk());
    }

}
