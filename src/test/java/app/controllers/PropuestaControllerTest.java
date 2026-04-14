package app.controllers;

import app.dto.PropuestaDto;
import app.model.entities.EstadoProceso;
import app.servicios.PropuestaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PropuestaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PropuestaService propuestaService;

    @Test
    void crearPropuestaDevuelve201() throws Exception {
        PropuestaDto dto = new PropuestaDto(
            "uuid-123", "1000", "1001", "ARG-10",
            List.of("FRA-10"), EstadoProceso.PENDIENTE);

        when(propuestaService.crearPropuesta(any())).thenReturn(dto);

        String json = """
        {
            "usuario_origen_id": "1000",
            "usuario_destino_id": "1001",
            "figurita_buscada_id": "ARG-10",
            "figuritas_ofrecidas_ids": ["FRA-10"]
        }
        """;

        mockMvc.perform(post("/propuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is(201));
    }

    @Test
    void responderPropuestaNoFalla() throws Exception {
        mockMvc.perform(patch("/propuestas/1")).andExpect(status().isOk());
    }

}
