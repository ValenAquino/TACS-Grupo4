package app.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ControladorPerfilTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void getSugerencias_retorna200() throws Exception {
    mockMvc.perform(
            get("/perfil/u-1000/sugerencias")
                .param("tipo", "1a1")
                .param("paginaActual", "0")
                .param("limite", "10")
        )
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void getNotificaciones_retorna200() throws Exception {
    mockMvc.perform(get("/perfil/1000/notificaciones"))
        .andExpect(status().isOk());
  }

  @Test
  void calificarUsuarioNoFalla() throws Exception {
    mockMvc.perform(post("/perfil/1000/calificaciones")
            .contentType("application/json")
            .content("""
              {
                "user_id": "1001",
                "valor": 4,
                "descripcion": "Buen intercambio",
                "transaction_id": "i-1",
                "tipo_transaccion": "INTERCAMBIO"
              }
          """))
        .andExpect(status().isOk());
  }


  @Test
  void getIntercambiables_usuarioExistente_retorna200() throws Exception {
    mockMvc.perform(get("/perfil/1000/intercambiables"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void getIntercambiables_usuarioInexistente_retorna404() throws Exception {
    mockMvc.perform(get("/perfil/u-99/intercambiables"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getFaltantes_usuarioExistente_retorna200() throws Exception {
    mockMvc.perform(get("/perfil/u-1000/faltantes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void getFaltantes_usuarioInexistente_retorna404() throws Exception {
    mockMvc.perform(get("/perfil/u-99/faltantes"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getRepetidas_usuarioExistente_retorna200() throws Exception {
    mockMvc.perform(get("/perfil/u-1000/repetidas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void getRepetidas_usuarioInexistente_retorna404() throws Exception {
    mockMvc.perform(get("/perfil/u-99/repetidas"))
        .andExpect(status().isNotFound());
  }
}
