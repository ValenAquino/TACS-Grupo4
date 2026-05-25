package app.controllers;

import app.servicios.ServicioJwt;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ControladorUsuarioTest {
  @Autowired
  MockMvc mockMvc;

  @MockBean
  ServicioJwt servicioJwt;

  @Test
  void editarContraseniaDevuelve400SiBodyVacio() throws Exception {
    when(servicioJwt.getPerfilId(any())).thenReturn("perfil-id-test");

    mockMvc.perform(put("/usuarios/contrasenia")
            .cookie(new Cookie("token", "token-de-prueba"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().is(400));
  }

  @Test
  void crearUsuarioNoFalla() throws Exception {
    String json = """
        {
            "nombre": "lucas",
            "contrasenia": "gordo123",
            "rol": "USUARIO"
        }
        """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().is(204));
  }
}
