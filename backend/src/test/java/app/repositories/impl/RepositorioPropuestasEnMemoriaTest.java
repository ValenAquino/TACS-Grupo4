package app.repositories.impl;

import app.model.entities.Coleccion;
import app.model.entities.EstadoProceso;
import app.model.entities.EstadoPropuesta;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Propuesta;
import app.model.entities.Perfil;
import app.model.entities.Rol;
import app.model.entities.Usuario;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioPropuestasEnMemoriaTest {

    private RepositorioPropuestasEnMemoria repositorio;
    private Perfil u1;
    private Perfil u2;
    private Perfil u3;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioPropuestasEnMemoria();

        Usuario user = new Usuario("u-1", Rol.USUARIO,"lucas", "fiscella");
        u1 = Perfil.builder()
            .id("1").usuario(user).nombre("Lucas")
            .mediosDeContacto(telegram("@lucas"))
            .build();

        user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
        u2 = Perfil.builder()
            .id("2").usuario(user).nombre("Sofía")
            .mediosDeContacto(telegram("@sofia"))
            .build();

        user = new Usuario("u-3",  Rol.USUARIO,"lucas", "fiscella");

        u3 = Perfil.builder()
            .id("3").usuario(user).nombre("Matías")
            .mediosDeContacto(telegram("@matias"))
            .build();
    }

    @Test
    void findByOrigenId_retornaSoloPropuestasDelOrigen() {
        Propuesta p1 = new Propuesta("p-1", u1, u2, new ArrayList<>(), null,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))));
        Propuesta p2 = new Propuesta("p-2", u2, u3, new ArrayList<>(), null,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))));

        repositorio.guardar(p1);
        repositorio.guardar(p2);

        List<Propuesta> resultado = repositorio.buscarPorAutorId("1");

        assertEquals(1, resultado.size());
        assertEquals("p-1", resultado.get(0).getId());
    }

    @Test
    void findByDestinoId_retornaSoloPropuestasDelDestino() {
        Propuesta p1 = new Propuesta("p-1", u1, u2, new ArrayList<>(), null,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))));
        Propuesta p2 = new Propuesta("p-2", u2, u3, new ArrayList<>(), null,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))));

        repositorio.guardar(p1);
        repositorio.guardar(p2);

        List<Propuesta> resultado = repositorio.buscarPorDestinatarioId("2");

        assertEquals(1, resultado.size());
        assertEquals("p-1", resultado.get(0).getId());
    }

    @Test
    void findByOrigenId_sinResultados_retornaListaVacia() {
        assertTrue(repositorio.buscarPorAutorId("u-99").isEmpty());
    }
}