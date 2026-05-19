package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import app.dto.EstadisticasDto;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Propuesta;
import app.model.entities.Rol;
import app.model.entities.Seleccion;
import app.model.entities.Subasta;
import app.model.entities.Usuario;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioPerfiles;
import app.servicios.ServicioEstadisticas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ServicioEstadisticaTest {

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

    private Figurita figurita(String id, Seleccion seleccion) {
        return new Figurita(id, 1, "Jugador", seleccion, "Delantero");
    }

    private FiguritaIntercambiable soloIntercambio(Figurita f) {
        return new FiguritaIntercambiable(f, 1, List.of(MetodoIntercambio.INTERCAMBIO));
    }

    private FiguritaIntercambiable soloSubasta(Figurita f) {
        return new FiguritaIntercambiable(f, 1, List.of(MetodoIntercambio.SUBASTA));
    }

    private FiguritaIntercambiable ambosMetodos(Figurita f) {
        return new FiguritaIntercambiable(f, 1, List.of(MetodoIntercambio.INTERCAMBIO, MetodoIntercambio.SUBASTA));
    }

    private void stubRepositoriosVacios() {
        when(repositorioUsuarios.contar()).thenReturn(0);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of());
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of());
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());
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
        Figurita f1 = figurita("f1", Seleccion.ARGENTINA);
        Figurita f2 = figurita("f2", Seleccion.BRASIL);
        Figurita f3 = figurita("f3", Seleccion.ARGENTINA);

        Coleccion coleccionConDos = new Coleccion();
        coleccionConDos.getRepetidas().add(soloIntercambio(f1));
        coleccionConDos.getRepetidas().add(soloIntercambio(f2));

        Coleccion coleccionConUna = new Coleccion();
        coleccionConUna.getRepetidas().add(soloIntercambio(f3));

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

        Subasta subastaActiva = Subasta.builder().id("s-1").autor(u1).fechaInicio(
                LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
            .build();;

        when(repositorioUsuarios.contar()).thenReturn(2L);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1, u2));
        when(repositorioPropuestas.contar()).thenReturn(4);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of());
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

        Subasta activa  = Subasta.builder().id("s-1").autor(u1).fechaInicio(
                LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
            .build();
        Subasta vencida = Subasta.builder().id("s-2").autor(u1).fechaInicio(
                LocalDateTime.now().minusDays(3)).fechaCierre(LocalDateTime.now().minusDays(1))
            .build();

        when(repositorioUsuarios.contar()).thenReturn(1L);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1));
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of());
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of(activa, vencida));

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(1, resultado.getTotalSubastasActivas());
    }

    @Test
    void propuestasPorEstado_sinPropuestas_retornaCeros() {
        stubRepositoriosVacios();

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(0, resultado.getPropuestasPorEstado().getPendientes());
        assertEquals(0, resultado.getPropuestasPorEstado().getAceptadas());
        assertEquals(0, resultado.getPropuestasPorEstado().getRechazadas());
    }

    @Test
    void propuestasPorEstado_conPropuestasMixtas_retornaConteoCorrecto() {
        Perfil autor = perfil("a", "usr-a", "Autor");
        Perfil destinatario = perfil("d", "usr-d", "Dest");

        Propuesta pendiente = new Propuesta("p1", autor, destinatario, List.of(), null);

        Propuesta aceptada = new Propuesta("p2", autor, destinatario, List.of(), null);
        aceptada.aceptar(destinatario);

        Propuesta rechazada = new Propuesta("p3", autor, destinatario, List.of(), null);
        rechazada.rechazar(destinatario);

        when(repositorioUsuarios.contar()).thenReturn(0);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of());
        when(repositorioPropuestas.contar()).thenReturn(3);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of(pendiente, aceptada, rechazada));
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(1, resultado.getPropuestasPorEstado().getPendientes());
        assertEquals(1, resultado.getPropuestasPorEstado().getAceptadas());
        assertEquals(1, resultado.getPropuestasPorEstado().getRechazadas());
    }

    @Test
    void propuestasPorEstado_variasDelMismoEstado_acumulaCorrectamente() {
        Perfil autor = perfil("a", "usr-a", "Autor");
        Perfil destinatario = perfil("d", "usr-d", "Dest");

        Propuesta p1 = new Propuesta("p1", autor, destinatario, List.of(), null);
        Propuesta p2 = new Propuesta("p2", autor, destinatario, List.of(), null);
        Propuesta p3 = new Propuesta("p3", autor, destinatario, List.of(), null);
        p3.aceptar(destinatario);

        when(repositorioUsuarios.contar()).thenReturn(0);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of());
        when(repositorioPropuestas.contar()).thenReturn(3);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of(p1, p2, p3));
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(2, resultado.getPropuestasPorEstado().getPendientes());
        assertEquals(1, resultado.getPropuestasPorEstado().getAceptadas());
        assertEquals(0, resultado.getPropuestasPorEstado().getRechazadas());
    }

    @Test
    void figuritasPorModalidad_sinFiguritas_retornaCeros() {
        stubRepositoriosVacios();

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(0, resultado.getFiguritasPorModalidad().getSoloIntercambio());
        assertEquals(0, resultado.getFiguritasPorModalidad().getSoloSubasta());
        assertEquals(0, resultado.getFiguritasPorModalidad().getAmbos());
    }

    @Test
    void figuritasPorModalidad_conFiguritasMixtas_retornaConteoCorrecto() {
        Figurita f1 = figurita("f1", Seleccion.ARGENTINA);
        Figurita f2 = figurita("f2", Seleccion.BRASIL);
        Figurita f3 = figurita("f3", Seleccion.ESPAÑA);

        Coleccion coleccion = new Coleccion();
        coleccion.getRepetidas().add(soloIntercambio(f1));
        coleccion.getRepetidas().add(soloSubasta(f2));
        coleccion.getRepetidas().add(ambosMetodos(f3));

        Perfil u1 = new Perfil("u-1", new Usuario("usr-1", Rol.USUARIO), "Lucas",
            coleccion, telegram("@lucas"), new ArrayList<>());

        when(repositorioUsuarios.contar()).thenReturn(1);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1));
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of());
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(1, resultado.getFiguritasPorModalidad().getSoloIntercambio());
        assertEquals(1, resultado.getFiguritasPorModalidad().getSoloSubasta());
        assertEquals(1, resultado.getFiguritasPorModalidad().getAmbos());
    }

    @Test
    void topSelecciones_sinFiguritas_retornaListaVacia() {
        stubRepositoriosVacios();

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertTrue(resultado.getTopSelecciones().isEmpty());
    }

    @Test
    void topSelecciones_retornaTop3OrdenadoDescendente() {
        Coleccion coleccion = new Coleccion();
        coleccion.getRepetidas().add(soloIntercambio(figurita("f1", Seleccion.ARGENTINA)));
        coleccion.getRepetidas().add(soloIntercambio(figurita("f2", Seleccion.ARGENTINA)));
        coleccion.getRepetidas().add(soloIntercambio(figurita("f3", Seleccion.ARGENTINA)));
        coleccion.getRepetidas().add(soloIntercambio(figurita("f4", Seleccion.BRASIL)));
        coleccion.getRepetidas().add(soloIntercambio(figurita("f5", Seleccion.BRASIL)));
        coleccion.getRepetidas().add(soloIntercambio(figurita("f6", Seleccion.ESPAÑA)));

        Perfil u1 = new Perfil("u-1", new Usuario("usr-1", Rol.USUARIO), "Lucas",
            coleccion, telegram("@lucas"), new ArrayList<>());

        when(repositorioUsuarios.contar()).thenReturn(1);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1));
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of());
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(3, resultado.getTopSelecciones().size());
        assertEquals("ARGENTINA", resultado.getTopSelecciones().get(0).getSeleccion());
        assertEquals(3, resultado.getTopSelecciones().get(0).getCantidad());
        assertEquals("BRASIL", resultado.getTopSelecciones().get(1).getSeleccion());
        assertEquals(2, resultado.getTopSelecciones().get(1).getCantidad());
        assertEquals("ESPAÑA", resultado.getTopSelecciones().get(2).getSeleccion());
        assertEquals(1, resultado.getTopSelecciones().get(2).getCantidad());
    }

    @Test
    void topSelecciones_menosDeTresSelecciones_retornaSoloLasExistentes() {
        Coleccion coleccion = new Coleccion();
        coleccion.getRepetidas().add(soloIntercambio(figurita("f1", Seleccion.ARGENTINA)));
        coleccion.getRepetidas().add(soloIntercambio(figurita("f2", Seleccion.BRASIL)));

        Perfil u1 = new Perfil("u-1", new Usuario("usr-1", Rol.USUARIO), "Lucas",
            coleccion, telegram("@lucas"), new ArrayList<>());

        when(repositorioUsuarios.contar()).thenReturn(1);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of(u1));
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioPropuestas.buscarTodos()).thenReturn(List.of());
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());

        EstadisticasDto resultado = service.obtenerEstadisticas();

        assertEquals(2, resultado.getTopSelecciones().size());
    }
}