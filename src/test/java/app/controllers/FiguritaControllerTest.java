package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FiguritaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getFiguritasNoFalla() throws Exception {
        mockMvc.perform(get("/figuritas")).andExpect(status().isOk());
    }

    @Test
    void getSugerenciasNoFalla() throws Exception {
        mockMvc.perform(get("/sugerencias")).andExpect(status().isOk());
    }

}
