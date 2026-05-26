package app.controllers;

import app.dto.request.UsuarioRequest;
import app.model.entities.Rol;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioUsuarios;
import app.servicios.ServicioJwt;
import app.servicios.ServicioUsuario;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

  @MockBean
  RepositorioUsuarios repositorioUsuarios;

  @MockBean
  RepositorioPerfiles repositorioPerfiles;

  @MockBean
  RepositorioColecciones repositorioColecciones;

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
  @Test
  void crearUsuarioFalla_nombreNull() throws Exception {
    String json = """
      {
          "nombre": null,
          "contrasenia": "gordo123",
          "rol": "USUARIO"
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void crearUsuarioFalla_nombreVacio() throws Exception {
    String json = """
      {
          "nombre": "",
          "contrasenia": "gordo123",
          "rol": "USUARIO"
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void crearUsuarioFalla_nombreEspacios() throws Exception {
    String json = """
      {
          "nombre": "   ",
          "contrasenia": "gordo123",
          "rol": "USUARIO"
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void crearUsuarioFalla_contraseniaNull() throws Exception {
    String json = """
      {
          "nombre": "lucas",
          "contrasenia": null,
          "rol": "USUARIO"
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void crearUsuarioFalla_contraseniaVacia() throws Exception {
    String json = """
      {
          "nombre": "lucas",
          "contrasenia": "",
          "rol": "USUARIO"
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void crearUsuarioFalla_contraseniaEspacios() throws Exception {
    String json = """
      {
          "nombre": "lucas",
          "contrasenia": "   ",
          "rol": "USUARIO"
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void crearUsuarioFalla_rolNull() throws Exception {
    String json = """
      {
          "nombre": "lucas",
          "contrasenia": "gordo123",
          "rol": null
      }
      """;

    mockMvc.perform(post("/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editarContraseniaNoFalla() throws Exception {

    when(servicioJwt.getPerfilId(anyString()))
        .thenReturn("perfil-id-test");

    String passwordEncriptada =
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
            .encode("vieja123");

    app.model.entities.Usuario usuario =
        new app.model.entities.Usuario(
            "user-id",
            Rol.USUARIO,
            "lucas",
            passwordEncriptada
        );

    app.model.entities.Perfil perfil =
        app.model.entities.Perfil.builder()
            .usuario(usuario)
            .build();

    when(repositorioPerfiles.buscarPorId(anyString(), any()))
        .thenReturn(perfil);

    mockMvc.perform(put("/usuarios/contrasenia")
            .cookie(new Cookie("token", "token-de-prueba"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "contrasenia_actual": "vieja123",
                    "contrasenia_nueva": "nueva123"
                }
                """))
        .andExpect(status().isOk());
  }


  @Test
  void editarContraseniaFalla_contraseniaActualNull() throws Exception {
    when(servicioJwt.getPerfilId(any())).thenReturn("perfil-id-test");

    mockMvc.perform(put("/usuarios/contrasenia")
            .cookie(new Cookie("token", "token-de-prueba"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                  "contrasenia_actual": null,
                  "contrasenia_nueva": "nueva123"
              }
              """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editarContraseniaFalla_contraseniaActualVacia() throws Exception {
    when(servicioJwt.getPerfilId(any())).thenReturn("perfil-id-test");

    mockMvc.perform(put("/usuarios/contrasenia")
            .cookie(new Cookie("token", "token-de-prueba"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                  "contrasenia_actual": "",
                  "contrasenia_nueva": "nueva123"
              }
              """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editarContraseniaFalla_contraseniaNuevaNull() throws Exception {
    when(servicioJwt.getPerfilId(any())).thenReturn("perfil-id-test");

    mockMvc.perform(put("/usuarios/contrasenia")
            .cookie(new Cookie("token", "token-de-prueba"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                  "contrasenia_actual": "vieja123",
                  "contrasenia_nueva": null
              }
              """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editarContraseniaFalla_contraseniaNuevaVacia() throws Exception {
    when(servicioJwt.getPerfilId(any())).thenReturn("perfil-id-test");

    mockMvc.perform(put("/usuarios/contrasenia")
            .cookie(new Cookie("token", "token-de-prueba"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                  "contrasenia_actual": "vieja123",
                  "contrasenia_nueva": ""
              }
              """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void registrarAdministradorNoFalla() throws Exception {

    when(servicioJwt.getRol(anyString()))
        .thenReturn("ADMINISTRADOR");

    String json = """
        {
            "nombre": "admin",
            "contrasenia": "admin123",
            "rol": "USUARIO"
        }
        """;

    mockMvc.perform(post("/administradores")
            .cookie(new Cookie("token", "token-admin"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isNoContent());
  }
}
