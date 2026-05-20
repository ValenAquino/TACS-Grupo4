package app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ControladorUsuarioTest {
  @Autowired
  MockMvc mockMvc;

  @Test
  void agregarRepetidaNoFalla() throws Exception {
    String json = """
        {
            "nombre": "lucas",
            "contrasenia": "gordo123",
            "rol": "USUARIO"
        }
        """;

    mockMvc.perform(post("/registrar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().is(204));
  }
}
