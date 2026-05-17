package app.servicios.impl;

import app.dto.EstadisticasDto;
import app.model.entities.*;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioPerfiles;
import app.servicios.ServicioEstadisticas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticasServiceImplTest {

    @Mock private RepositorioPerfiles repositorioUsuarios;
    @Mock private RepositorioPropuestas repositorioPropuestas;
    @Mock private RepositorioSubastas repositorioSubastas;

    private ServicioEstadisticas service;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    private Perfil perfil(String id, String usuarioId, String nombre) {
        Usuario user = new Usuario(usuarioId, Rol.USUARIO, "lucas", "fiscella");
        return Perfil.builder()
            .id(id).usuario(user).nombre("nombre")
            .mediosDeContacto(telegram("@" + nombre.toLowerCase()))
            .build();
    }

    @BeforeEach
    void setUp() {
        service = new ServicioEstadisticas(repositorioUsuarios, repositorioPropuestas, repositorioSubastas);
    }

    @Test
    void getEstadisticas_sinDatos_retornaTodosCeros() {
        when(repositorioUsuarios.contar()).thenReturn(0L);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of());
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(0, resultado.getTotalUsuarios());
        assertEquals(0, resultado.getTotalFiguritasPublicadas());
        assertEquals(0, resultado.getTotalPropuestas());
        assertEquals(0, resultado.getTotalSubastasActivas());
    }

    @Test
    void getEstadisticas_conDatos_retornaValoresCorrectos() {
        Coleccion coleccionConDos = new Coleccion();
        coleccionConDos.getRepetidas().add(new FiguritaIntercambiable(null, 1, List.of(MetodoIntercambio.INTERCAMBIO)));
        coleccionConDos.getRepetidas().add(new FiguritaIntercambiable(null, 2, List.of(MetodoIntercambio.INTERCAMBIO)));

        Coleccion coleccionConUna = new Coleccion();
        coleccionConUna.getRepetidas().add(new FiguritaIntercambiable(null, 3, List.of(MetodoIntercambio.INTERCAMBIO)));

        Usuario user =  new Usuario("usr-1", Rol.USUARIO, "lucas", "fiscella");
        Perfil u1 = Perfil.builder()
            .id("u-1").usuario(user).nombre("Lucas")
            .mediosDeContacto(telegram("@lucas"))
            .coleccion(coleccionConDos)
            .build();

        user = new Usuario("usr-2", Rol.USUARIO, "lucas", "fiscella");
        Perfil u2 = Perfil.builder()
            .id("u-2").usuario(user).nombre("Sofía")
            .mediosDeContacto(telegram("@sofia"))
            .coleccion(coleccionConUna)
            .build();

        Subasta subastaActiva = new Subasta("s-1", u1,
            LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(2), null);

        when(repositorioUsuarios.contar()).thenReturn(2L);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1, u2));
        when(repositorioPropuestas.contar()).thenReturn(4);
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of(subastaActiva));

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(2, resultado.getTotalUsuarios());
        assertEquals(3, resultado.getTotalFiguritasPublicadas());
        assertEquals(4, resultado.getTotalPropuestas());
        assertEquals(1, resultado.getTotalSubastasActivas());
    }

    @Test
    void getEstadisticas_filtraSoloSubastasActivas() {
        Perfil u1 = perfil("u-1", "usr-1", "Lucas");

        Subasta activa  = new Subasta("s-1", u1,
            LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(2), null);
        Subasta vencida = new Subasta("s-2", u1,
            LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1), null);

        when(repositorioUsuarios.contar()).thenReturn(1L);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1));
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of(activa, vencida));

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(1, resultado.getTotalSubastasActivas());
    }
}