package app.repositories.impl;

import app.MongoTestBase;
import app.model.entities.Coleccion;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Rol;
import app.model.entities.Subasta;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioSubastasTest extends MongoTestBase {

    @Autowired
    private RepositorioSubastas repositorio;
    @Autowired
    private RepositorioPerfiles repositorioPerfiles;
    @Autowired
    private RepositorioUsuarios repositorioUsuarios;
    @Autowired
    private RepositorioColecciones repositorioColecciones;

    private Perfil p1;
    private Perfil p2;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @BeforeEach
    void setUp() {

        Usuario user = new Usuario("u-1000", Rol.USUARIO,"lucas", "fiscella");
        Coleccion colec = new Coleccion("c-1000");
        repositorioColecciones.guardar(colec);
        repositorioUsuarios.guardar(user);
        p1 = Perfil.builder()
            .id("1").usuario(user).nombre("Lucas")
            .coleccion(colec)
            .mediosDeContacto(telegram("@lucas"))
            .build();
        repositorioPerfiles.guardar(p1);

        user = new Usuario("u-1001", Rol.USUARIO, "lucas", "fiscella");
        colec = new Coleccion("c-1001");
        repositorioColecciones.guardar(colec);
        repositorioUsuarios.guardar(user);
        p2 = Perfil.builder()
            .id("2").usuario(user).nombre("Sofía")
            .coleccion(colec)
            .mediosDeContacto(telegram("@sofia"))
            .build();
        repositorioPerfiles.guardar(p2);
    }

    @Test
    void findByUsuarioId_retornaSoloSubastasDelUsuario() {
        Subasta s1 = Subasta.builder().id("s-1").autor(p1).fechaInicio(
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
