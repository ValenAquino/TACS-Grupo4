package app.controllers;

import app.model.entities.EstadoProceso;
import app.model.entities.EstadoPropuesta;
import app.model.entities.Figurita;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Propuesta;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Usuario;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioPerfiles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PropuestaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RepositorioPropuestas repoPropuesta;

    @Test
    void crearPropuestaDevuelve201() throws Exception {
        String json = """
        {
            "autor_id": "1000",
            "destinatario_id": "1001",
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
        mockMvc.perform(patch("/propuestas/2000/aceptar")
                .header("usuario_id", "u-1001"))
            .andExpect(status().isNoContent());
    }

    @Test
    void rechazarPropuestaNoFalla() throws Exception {
        mockMvc.perform(patch("/propuestas/2000/rechazar")
                .header("usuario_id", "u-1001"))
            .andExpect(status().isNoContent());
    }
}