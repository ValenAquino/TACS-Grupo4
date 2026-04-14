package app.controllers;

import app.dto.FiguritaIntercambiableDto;
import app.model.entities.Seleccion;
import app.model.entities.MetodoIntercambio;
import app.servicios.impl.FiguritaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FiguritaControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private FiguritaService figuritaService;
    @Test
    void buscarFiguritasDevuelve200() throws Exception {
        FiguritaIntercambiableDto dto = new FiguritaIntercambiableDto(
            "ARG-10", 10, "Messi", Seleccion.ARGENTINA, 2,
            List.of(MetodoIntercambio.INTERCAMBIO), "1000");

        when(figuritaService.buscarFiguritas(null, null, null))
            .thenReturn(List.of(dto));

        mockMvc.perform(get("/figuritas")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
