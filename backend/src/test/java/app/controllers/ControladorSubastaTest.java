package app.controllers;

import app.InicializadorDeDatos;
import app.model.entities.*;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioSubastas;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControladorSubastaTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RepositorioPerfiles repositorioPerfiles;

    @MockBean
    RepositorioSubastas repositorioSubastas;

    @MockBean
    RepositorioFiguritas repositorioFiguritas;

    @MockBean
    InicializadorDeDatos inicializadorDeDatos;

    private Perfil sofia;
    private Perfil lucas;
    private Figurita messi;
    private Subasta subastaActiva;
    private Subasta subastaCerrada;

    @BeforeEach
    void setUp() {
        messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);

        Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
        sofia = Perfil.builder()
            .id("1").usuario(user).nombre("Sofía")
            .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, "@sofia")))
            .build();

        user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
        lucas = Perfil.builder()
            .id("2").usuario(user).nombre("Lucas")
            .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, "@lucas")))
            .build();

        subastaActiva =  Subasta.builder().id("s-1").autor(sofia).fechaInicio(
                LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
            .figuritaSubastada(messi)
            .build();

        subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
                LocalDateTime.now().minusDays(1)).fechaCierre(LocalDateTime.now().minusHours(1))
            .figuritaSubastada(messi)
            .build();
    }

    @Test
    void crearSubasta_retorna200() throws Exception {
        when(repositorioPerfiles.buscarPorUsuarioId("u-1")).thenReturn(sofia);
        when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);
        when(repositorioPerfiles.buscarPorFiguritaFaltante(messi)).thenReturn(List.of());

        mockMvc.perform(post("/subastas")
                .header("user_id", "u-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "figurita_id": "ARG-10",
                        "duracion_en_horas": 30
                    }
                """))
            .andExpect(status().isOk());
    }

    @Test
    void ofertarEnSubasta_retorna200() throws Exception {
        Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);

        when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(lucas);
        when(repositorioPerfiles.buscarPorId("1")).thenReturn(sofia);
        when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaActiva);
        when(repositorioFiguritas.buscarPorId("ARG-11")).thenReturn(diMaria);

        mockMvc.perform(post("/subastas/s-1/propuestas")
                .header("user_id", "u-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "usuario_id": "1",
                        "figuritas_ofrecidas_id": ["ARG-11"]
                    }
                """))
            .andExpect(status().isOk());
    }

    @Test
    void ofertarEnSubastaCerrada_retorna400() throws Exception {
        when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(lucas);
        when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaCerrada);

        mockMvc.perform(post("/subastas/s-2/propuestas")
                .header("user_id", "u-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "usuario_id": "1",
                        "figuritas_ofrecidas_id": ["ARG-11"]
                    }
                """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void seleccionarOferta_retorna200() throws Exception {
        Propuesta propuesta = new Propuesta("o-1", lucas, sofia, List.of(), messi);
        subastaActiva.getOfertas().add(propuesta);

        when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaActiva);

        mockMvc.perform(post("/subastas/s-1/ofertas/o-1/seleccionar")
                .header("user_id", "u-1"))
            .andExpect(status().isOk());
    }

    @Test
    void seleccionarOferta_subastaInactiva_retorna400() throws Exception {
        when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaCerrada);

        mockMvc.perform(post("/subastas/s-2/ofertas/o-1/seleccionar")
                .header("user_id", "u-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rechazarOferta_retorna200() throws Exception {
        Propuesta propuesta = new Propuesta("o-1", lucas, sofia, List.of(), messi);
        subastaActiva.getOfertas().add(propuesta);

        when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaActiva);

        mockMvc.perform(post("/subastas/s-1/ofertas/o-1/rechazar")
                .header("user_id", "u-1"))
            .andExpect(status().isOk());
    }

    @Test
    void rechazarOferta_subastaInactiva_retorna400() throws Exception {
        when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaCerrada);

        mockMvc.perform(post("/subastas/s-2/ofertas/o-1/rechazar")
                .header("user_id", "u-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void cancelarSubasta_retorna200() throws Exception {
        when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaActiva);

        mockMvc.perform(post("/subastas/s-1/cancelar")
                .header("user_id", "u-1"))
            .andExpect(status().isOk());
    }

    @Test
    void cancelarSubasta_subastaInactiva_retorna400() throws Exception {
        when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaCerrada);

        mockMvc.perform(post("/subastas/s-2/cancelar")
                .header("user_id", "u-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void cerrarSubasta_retorna200() throws Exception {
        Propuesta propuesta = new Propuesta("o-1", lucas, sofia, List.of(), messi);
        propuesta.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.SELECCIONADO));
        subastaActiva.getOfertas().add(propuesta);

        when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaActiva);

        mockMvc.perform(post("/subastas/s-1/cerrar")
                .header("user_id", "u-1"))
            .andExpect(status().isOk());
    }

    @Test
    void cerrarSubasta_sinOfertaSeleccionada_retorna400() throws Exception {
        Propuesta propuesta = new Propuesta("o-1", lucas, sofia, List.of(), messi);
        subastaActiva.getOfertas().add(propuesta);

        when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaActiva);

        mockMvc.perform(post("/subastas/s-1/cerrar")
                .header("user_id", "u-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void cerrarSubasta_subastaInactiva_retorna400() throws Exception {
        when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaCerrada);

        mockMvc.perform(post("/subastas/s-2/cerrar")
                .header("user_id", "u-1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerMisSubastas_retorna200() throws Exception {
        when(repositorioSubastas.buscarPorAutorUserId("u-1"))
            .thenReturn(List.of(subastaActiva));

        mockMvc.perform(get("/subastas/mis-subastas")
                .param("userId", "u-1"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerSubastasParticipo_retorna200() throws Exception {
        Propuesta propuesta = new Propuesta("o-1", lucas, sofia, List.of(), messi);
        subastaActiva.getOfertas().add(propuesta);

        when(repositorioSubastas.buscarDondeParticipa("u-2"))
            .thenReturn(List.of(subastaActiva));

        mockMvc.perform(get("/subastas/participo")
                .param("userId", "u-2"))
            .andExpect(status().isOk());
    }
}