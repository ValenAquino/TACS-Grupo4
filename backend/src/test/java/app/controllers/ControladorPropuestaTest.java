package app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.dto.PropuestaDto;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CrearPropuestaRequest;
import app.servicios.ServicioJwt;
import app.servicios.ServicioPropuesta;
import jakarta.servlet.http.Cookie;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControladorPropuestaTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ServicioPropuesta propuestaService;

    @MockBean
    ServicioJwt servicioJwt;

    private final Cookie cookie =
        new Cookie("token","fake-token");

    @BeforeEach
    void setup() {
        when(servicioJwt.getPerfilId("fake-token"))
            .thenReturn("1000");
    }

    @Test
    void crearPropuesta_retorna201() throws Exception {

        when(propuestaService.crearPropuesta(
            eq("1000"),
            any(CrearPropuestaRequest.class)
        )).thenReturn(null);

        mockMvc.perform(
                post("/propuestas")
                    .cookie(cookie)
                    .contentType("application/json")
                    .content("""
                    {
                      "destinatario_id":"2000",
                      "figurita_buscada_id":"ARG-10",
                      "figuritas_ofrecidas_ids":[
                        "ARG-1",
                        "ARG-2"
                      ]
                    }
                    """)
            )
            .andExpect(status().isCreated());

        verify(propuestaService)
            .crearPropuesta(
                eq("1000"),
                any(CrearPropuestaRequest.class)
            );
    }

    @Test
    void aceptarPropuesta_retorna204() throws Exception {

        mockMvc.perform(
                patch("/propuestas/p-1/aceptar")
                    .cookie(cookie)
            )
            .andExpect(status().isNoContent());

        verify(propuestaService)
            .aceptar("p-1","1000");
    }

    @Test
    void rechazarPropuesta_retorna204() throws Exception {

        mockMvc.perform(
                patch("/propuestas/p-1/rechazar")
                    .cookie(cookie)
            )
            .andExpect(status().isNoContent());

        verify(propuestaService)
            .rechazar("p-1","1000");
    }

    @Test
    void cancelarPropuesta_retorna204() throws Exception {

        mockMvc.perform(
                patch("/propuestas/p-1/cancelar")
                    .cookie(cookie)
            )
            .andExpect(status().isNoContent());

        verify(propuestaService)
            .cancelar("p-1","1000");
    }

    @Test
    void obtenerPropuestas_retorna200() throws Exception {

        when(propuestaService.buscarPropuestas(
            eq("1000"),
            any()
        )).thenReturn(
            new PaginaResultado<>(
                List.of(),
                0,
                0,
                0
            )
        );

        mockMvc.perform(
                get("/propuestas")
                    .cookie(cookie)
            )
            .andExpect(status().isOk());

        verify(propuestaService)
            .buscarPropuestas(
                eq("1000"),
                any()
            );
    }
}