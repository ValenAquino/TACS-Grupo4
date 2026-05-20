package app.model.entities;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubastaTest {

    private Perfil usuario;

    @BeforeEach
    void setUp() {

        Usuario user = new Usuario("u-1000", Rol.USUARIO, "lucas", "fiscella");
        usuario = Perfil.builder()
            .id("1").usuario(user).nombre("Lucas")
            .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, "@lucas")))
            .build();

    }

    @Test
    void estaActivo_cuandoSubastaActiva_retornaTrue() {
        Subasta subasta = Subasta.builder().id("s-1").autor(usuario).fechaInicio(
                LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(2))
            .build();

        assertTrue(subasta.estaActivo());
    }

    @Test
    void estaActivo_cuandoSubastaVencida_retornaFalse() {
        Subasta subasta = Subasta.builder().id("s-2").autor(usuario).fechaInicio(
                LocalDateTime.now().minusDays(3)).fechaCierre(LocalDateTime.now().minusDays(1))
            .build();

        assertFalse(subasta.estaActivo());
    }

    @Test
    void estaActivo_cuandoSubastaNoIniciada_retornaFalse() {
        Subasta subasta = Subasta.builder().id("s-3").autor(usuario).fechaInicio(
                LocalDateTime.now().plusDays(1)).fechaCierre(LocalDateTime.now().plusDays(3))
            .build();

        assertFalse(subasta.estaActivo());
    }
}