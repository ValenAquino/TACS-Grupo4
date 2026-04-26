package app.controllers;

import app.model.entities.EstadoProceso;
import app.model.entities.EstadoPropuesta;
import app.model.entities.Figurita;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Propuesta;
import app.model.entities.Perfil;
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

    @MockBean RepositorioPerfiles repoUser;
    @MockBean RepositorioFiguritas repoFigurita;
    @MockBean RepositorioPropuestas repoPropuesta;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @Test
    void crearPropuestaDevuelve201() throws Exception {
        Perfil subastador    = new Perfil("1000", "", null, telegram("@subastador"),    null);
        Perfil userPropuesta = new Perfil("1001", "", null, telegram("@userPropuesta"), null);

        Figurita buscada  = new Figurita("ARG-10", 2, null, null);
        Figurita ofrecida = new Figurita("FRA-10", 2, null, null);

        String json = """
        {
            "autor_id": "1000",
            "destinatario_id": "1001",
            "figurita_buscada_id": "ARG-10",
            "figuritas_ofrecidas_ids": ["FRA-10"]
        }
        """;

        when(repoUser.buscarPorId("1000")).thenReturn(subastador);
        when(repoUser.buscarPorId("1001")).thenReturn(userPropuesta);
        when(repoFigurita.buscarPorId("ARG-10")).thenReturn(buscada);
        when(repoFigurita.buscarPorId("FRA-10")).thenReturn(ofrecida);

        mockMvc.perform(post("/propuestas")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated());
    }

    @Test
    void aceptarPropuestaNoFalla() throws Exception {
        Perfil subastador    = new Perfil("1000", "", null, telegram("@subastador"),    null);
        Perfil userPropuesta = new Perfil("1001", "", null, telegram("@userPropuesta"), null);

        Propuesta propuesta = new Propuesta("1000", subastador, userPropuesta,
            new ArrayList<>(), null,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))));

        when(repoPropuesta.buscarPorId("1000")).thenReturn(propuesta);

        mockMvc.perform(patch("/propuestas/1000/aceptar"))
            .andExpect(status().isNoContent());
    }

    @Test
    void rechazarPropuestaNoFalla() throws Exception {
        Perfil subastador    = new Perfil("1000", "", null, telegram("@subastador"),    null);
        Perfil userPropuesta = new Perfil("1001", "", null, telegram("@userPropuesta"), null);

        Propuesta propuesta = new Propuesta("1000", subastador, userPropuesta,
            new ArrayList<>(), null,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))));

        when(repoPropuesta.buscarPorId("1000")).thenReturn(propuesta);

        mockMvc.perform(patch("/propuestas/1000/rechazar"))
            .andExpect(status().isNoContent());
    }
}