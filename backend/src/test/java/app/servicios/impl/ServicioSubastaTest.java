package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import app.exceptions.BadRequestException;
import app.model.entities.*;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioNotificaciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioSubastas;
import app.repositories.impl.RepositorioNotificacionesEnMemoria;
import java.time.LocalDateTime;
import java.util.List;

import app.servicios.ServicioNotificacion;
import app.servicios.ServicioSubasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ServicioSubastaTest {

  @Mock
  private RepositorioPerfiles repositorioPerfiles;
  @Mock
  private RepositorioSubastas repositorioSubastas;
  @Mock
  private RepositorioFiguritas repositorioFiguritas;

  private RepositorioNotificaciones repositorioNotificaciones;
  private ServicioSubasta service;

  private Perfil lucas;
  private Perfil sofia;
  private Figurita messi;

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  @BeforeEach
  void setUp() {
    this.repositorioNotificaciones = new RepositorioNotificacionesEnMemoria();
    ServicioNotificacion serviceNotificacion = new ServicioNotificacion(repositorioNotificaciones);
    service = new ServicioSubasta(repositorioSubastas, repositorioPerfiles,
        repositorioFiguritas, serviceNotificacion);

    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);

    Coleccion coleccionSinMessi = new Coleccion();
    coleccionSinMessi.getFaltantes().add(messi);

    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    lucas = Perfil.builder()
        .id("1").usuario(user).nombre("Lucas")
        .coleccion(coleccionSinMessi)
        .mediosDeContacto(telegram("@lucas"))
        .build();


    Coleccion coleccionRepetidos = new Coleccion();
    coleccionRepetidos.getRepetidas().add(new FiguritaIntercambiable(messi, 1, List.of(MetodoIntercambio.INTERCAMBIO)));

    user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    sofia = Perfil.builder()
        .id("2").usuario(user).nombre("Sofía")
        .coleccion(coleccionRepetidos)
        .mediosDeContacto(telegram("@sofia"))
        .build();
  }

  @Test
  void crearSubastaNotificaUsuarios() {
    when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(sofia);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);
    when(repositorioPerfiles.buscarPorFiguritaFaltante(messi)).thenReturn(List.of(lucas));

    service.crearSubasta("u-2", "ARG-10", 30, List.of(), 0);
    assertEquals(1, repositorioNotificaciones.buscarPorPerfil(lucas).size());
  }

  @Test
  void crearSubastaNoNotificaUsuarios() {
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);

    when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(sofia);
    when(repositorioFiguritas.buscarPorId("ARG-11")).thenReturn(diMaria);
    when(repositorioPerfiles.buscarPorFiguritaFaltante(diMaria)).thenReturn(List.of());

    service.crearSubasta("u-2", "ARG-11", 30, List.of(), 0);
    assertEquals(0, repositorioNotificaciones.buscarPorPerfil(lucas).size());
  }

  @Test
  void ofertarEnSubastaCerrada_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
        LocalDateTime.now().minusDays(3)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    when(repositorioPerfiles.buscarPorUsuarioId("u-1")).thenReturn(lucas);
    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.ofertarEnSubasta("u-1", "s-1", List.of("ARG-11")));
  }

  @Test
  void ofertarEnSubastaConFiguritasDuplicadas_lanzaExcepcion() {
    Subasta subastaActiva = Subasta.builder().id("s-2").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    when(repositorioPerfiles.buscarPorUsuarioId("u-1")).thenReturn(lucas);
    when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaActiva);

    assertThrows(BadRequestException.class,
        () -> service.ofertarEnSubasta("u-1","s-2", List.of("ARG-11", "ARG-11")));
  }

  @Test
  void seleccionarOferta_marcaComoSeleccionada() {
    Subasta subasta = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuesta = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();;
    subasta.agregarOferta(propuesta);

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subasta);

    service.seleccionarOferta("s-1", "o-1");

    assertEquals(EstadoProceso.SELECCIONADO, propuesta.obtenerEstadoActual().getValor());
  }

  @Test
  void seleccionarOferta_desseleccionaAnterior() {
    Subasta subasta = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuestaAnterior = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();;
    propuestaAnterior.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.SELECCIONADO));

    Propuesta propuestaNueva = Propuesta.builder()
        .id("o-2").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();;
    subasta.getOfertas().add(propuestaAnterior);
    subasta.getOfertas().add(propuestaNueva);

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subasta);

    service.seleccionarOferta("s-1", "o-2");

    assertEquals(EstadoProceso.PENDIENTE, propuestaAnterior.obtenerEstadoActual().getValor());
    assertEquals(EstadoProceso.SELECCIONADO, propuestaNueva.obtenerEstadoActual().getValor());
  }

  @Test
  void seleccionarOferta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.seleccionarOferta("s-1", "o-1"));
  }

  @Test
  void rechazarOferta_marcaComoRechazada() {
    Subasta subasta = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuesta = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    subasta.getOfertas().add(propuesta);

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subasta);

    service.rechazarOferta("s-1", "o-1");

    assertEquals(EstadoProceso.RECHAZADO, propuesta.obtenerEstadoActual().getValor());
  }

  @Test
  void rechazarOferta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();


    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.rechazarOferta("s-1", "o-1"));
  }

  @Test
  void cancelarSubasta_rechazaTodasLasOfertas() {
    Subasta subasta = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();


    Propuesta propuesta1 = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    Propuesta propuesta2 = Propuesta.builder()
        .id("o-2").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    subasta.getOfertas().add(propuesta1);
    subasta.getOfertas().add(propuesta2);

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subasta);

    service.cancelarSubasta("s-1");

    assertEquals(EstadoProceso.RECHAZADO, propuesta1.obtenerEstadoActual().getValor());
    assertEquals(EstadoProceso.RECHAZADO, propuesta2.obtenerEstadoActual().getValor());
    assertTrue(subasta.getFechaCierre().isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  void cancelarSubasta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.cancelarSubasta("s-1"));
  }

  @Test
  void cerrarSubasta_aceptaSeleccionadaYRechazaResto() {
    Subasta subasta = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuestaSeleccionada = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    propuestaSeleccionada.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.SELECCIONADO));

    Propuesta propuestaPendiente = Propuesta.builder()
        .id("o-2").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();

    subasta.getOfertas().add(propuestaSeleccionada);
    subasta.getOfertas().add(propuestaPendiente);

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subasta);

    service.cerrarSubasta("s-1");

    assertEquals(EstadoProceso.ACEPTADO, propuestaSeleccionada.obtenerEstadoActual().getValor());
    assertEquals(EstadoProceso.RECHAZADO, propuestaPendiente.obtenerEstadoActual().getValor());
    assertTrue(subasta.getFechaCierre().isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  void cerrarSubasta_sinOfertaSeleccionada_lanzaExcepcion() {
    Subasta subasta = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuesta = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    subasta.getOfertas().add(propuesta);

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subasta);

    assertThrows(BadRequestException.class,
        () -> service.cerrarSubasta("s-1"));
  }

  @Test
  void cerrarSubasta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.cerrarSubasta("s-1"));
  }
}