package app.repositories.impl;

import app.model.entities.Coleccion;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Subasta;
import app.model.entities.Perfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioSubastasEnMemoriaTest {

    private RepositorioSubastasEnMemoria repositorio;
    private Perfil u1;
    private Perfil u2;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioSubastasEnMemoria();
        u1 = new Perfil("u-1", "Lucas", new Coleccion(), telegram("@lucas"), new ArrayList<>());
        u2 = new Perfil("u-2", "Sofía", new Coleccion(), telegram("@sofia"), new ArrayList<>());
    }

    @Test
    void findByUsuarioId_retornaSoloSubastasDelUsuario() {
        Subasta s1 = new Subasta("s-1", u1,
            LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(2), null);
        Subasta s2 = new Subasta("s-2", u2,
            LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(2), null);

        repositorio.guardar(s1);
        repositorio.guardar(s2);

        List<Subasta> resultado = repositorio.buscarPorPerfilId("u-1");

        assertEquals(1, resultado.size());
        assertEquals("s-1", resultado.get(0).getId());
    }

    @Test
    void findByUsuarioId_sinResultados_retornaListaVacia() {
        assertTrue(repositorio.buscarPorPerfilId("u-99").isEmpty());
    }
}
