package app.controllers;

import app.dto.PerfilDto;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Usuario;
import app.repositories.RepositorioPerfiles;
import app.repositories.impl.campos.CamposPerfil;
import app.servicios.ServicioJwt;
import app.servicios.ServicioPerfil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ControladorSugerenciasTest {
  @Autowired
  MockMvc mockMvc;

  @MockBean
  RepositorioPerfiles perfilRepo;

  @MockBean
  ServicioJwt servicioJwt;

  private final Cookie cookie =
      new Cookie("token", "fake-token");

  @BeforeEach
  void setup() {
    Usuario usuario = new Usuario(
        "u-1",
        Rol.USUARIO,
        "juan",
        "1234"
    );

    Perfil perfil = Perfil.builder()
        .id("1000")
        .usuario(usuario)
        .nombre("Perfil 1")
        .calificacionMedia(0.0)
        .build();

    when(perfilRepo.buscarPorId("1000", new CamposPerfil(true)))
        .thenReturn(perfil);

    when(servicioJwt.getPerfilId("fake-token"))
        .thenReturn("1000");
  }

  @Test
  void buscarSugerenciasSinPaginacion_retorna200() throws Exception {
    mockMvc.perform(
            get("/sugerencias")
                .cookie(cookie)
                .contentType("application/json")
        )
        .andExpect(status().isOk());
  }
}
