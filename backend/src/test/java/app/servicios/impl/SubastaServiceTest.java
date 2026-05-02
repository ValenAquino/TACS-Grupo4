package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import app.exceptions.BadRequestException;
import app.model.entities.*;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioNotificaciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.impl.RepositorioNotificacionesEnMemoria;
import app.servicios.ISubastaService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SubastaServiceTest {

  @Mock
  private RepositorioPerfiles repositorioUsuarios;
  @Mock
  private RepositorioPropuestas repositorioPropuestas;
  @Mock
  private RepositorioSubastas repositorioSubastas;
  @Mock
  private RepositorioFiguritas repositorioFiguritas;

  private RepositorioNotificaciones repositorioNotificaciones;
  private ISubastaService service;

  private Perfil lucas;
  private Perfil sofia;
  private Figurita messi;

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  @BeforeEach
  void setUp() {
    this.repositorioNotificaciones = new RepositorioNotificacionesEnMemoria();
    NotificacionService serviceNotificacion = new NotificacionService(repositorioNotificaciones);
    service = new SubastaServiceImpl(repositorioSubastas, repositorioUsuarios,
        repositorioFiguritas, repositorioPropuestas, serviceNotificacion);

    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);

    Coleccion coleccionSinMessi = new Coleccion();
    coleccionSinMessi.getFaltantes().add(messi);
    lucas = new Perfil("1", new Usuario("u-1", Rol.USUARIO), "Lucas",
        coleccionSinMessi, telegram("@lucas"), new ArrayList<>());

    Coleccion coleccionRepetidos = new Coleccion();
    coleccionRepetidos.getRepetidas().add(new FiguritaIntercambiable(messi, 1, MetodoIntercambio.INTERCAMBIO));
    sofia = new Perfil("2", new Usuario("u-2", Rol.USUARIO), "Sofía",
        coleccionRepetidos, telegram("@sofia"), new ArrayList<>());
  }

  @Test
  void crearSubastaNotificaUsuarios() {
    LocalDateTime fechaInicio = LocalDateTime.now();

    when(repositorioUsuarios.buscarPorUsuarioId("u-2")).thenReturn(sofia);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);
    when(repositorioUsuarios.buscarPorFiguritaFaltante(messi)).thenReturn(List.of(lucas));

    service.crearSubasta(sofia.getUsuario().getId(), fechaInicio, fechaInicio.plusMinutes(30), "ARG-10");

    assertEquals(1, repositorioNotificaciones.buscarPorUsuario(lucas).size());
  }

  @Test
  void ofertarEnSubastaCerrada_lanzaExcepcion() {
    Subasta subastaCerrada = new Subasta("s-1", sofia,
        LocalDateTime.now().minusDays(2),
        LocalDateTime.now().minusDays(1),
        messi);

    when(repositorioUsuarios.buscarPorUsuarioId("u-1")).thenReturn(lucas);
    when(repositorioSubastas.buscarPorId("s-1")).thenReturn(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.ofertarEnSubasta("u-1", "2", "s-1", List.of("ARG-11")));
  }

  @Test
  void ofertarEnSubastaConFiguritasDuplicadas_lanzaExcepcion() {
    Subasta subastaActiva = new Subasta("s-2", sofia,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusDays(1),
        messi);

    when(repositorioUsuarios.buscarPorUsuarioId("u-1")).thenReturn(lucas);
    when(repositorioSubastas.buscarPorId("s-2")).thenReturn(subastaActiva);

    assertThrows(BadRequestException.class,
        () -> service.ofertarEnSubasta("u-1", "2", "s-2", List.of("ARG-11", "ARG-11")));
  }

  @Test
  void crearSubastaNoNotificaUsuarios() {
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA);
    LocalDateTime fechaInicio = LocalDateTime.now();

    when(repositorioUsuarios.buscarPorUsuarioId("u-2")).thenReturn(sofia);
    when(repositorioFiguritas.buscarPorId("ARG-11")).thenReturn(diMaria);
    when(repositorioUsuarios.buscarPorFiguritaFaltante(diMaria)).thenReturn(List.of());

    service.crearSubasta(sofia.getUsuario().getId(), fechaInicio, fechaInicio.plusMinutes(30), "ARG-11");

    assertEquals(0, repositorioNotificaciones.buscarPorUsuario(lucas).size());
  }
}