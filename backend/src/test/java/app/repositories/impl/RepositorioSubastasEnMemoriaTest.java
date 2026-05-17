package app.repositories.impl;

import app.model.entities.Coleccion;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Rol;
import app.model.entities.Subasta;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioSubastasEnMemoriaTest {

    private RepositorioSubastasEnMemoria repositorio;
    private Perfil p1;
    private Perfil p2;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioSubastasEnMemoria();

        Usuario user = new Usuario("u-1000", Rol.USUARIO,"lucas", "fiscella");
        p1 = Perfil.builder()
            .id("1").usuario(user).nombre("Lucas")
            .mediosDeContacto(telegram("@lucas"))
            .build();

        user = new Usuario("u-1001", Rol.USUARIO, "lucas", "fiscella");
        p2 = Perfil.builder()
            .id("2").usuario(user).nombre("Sofía")
            .mediosDeContacto(telegram("@sofia"))
            .build();
    }

    @Test
    void findByUsuarioId_retornaSoloSubastasDelUsuario() {
        Subasta s1 =  Subasta.builder().id("s-1").autor(p1).fechaInicio(
                LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
            .build();
        Subasta s2 = Subasta.builder().id("s-2").autor(p2).fechaInicio(
                LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
            .build();

        repositorio.guardar(s1);
        repositorio.guardar(s2);

        List<Subasta> resultado = repositorio.buscarPorAutorUsuarioId("u-1000");

        assertEquals(1, resultado.size());
        assertEquals("s-1", resultado.get(0).getId());
    }

    @Test
    void findByUsuarioId_sinResultados_retornaListaVacia() {
        assertTrue(repositorio.buscarPorAutorUsuarioId("u-99").isEmpty());
    }
}
