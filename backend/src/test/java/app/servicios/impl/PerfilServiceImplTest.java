package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.dto.CalificacionDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.OperacionesDto;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.model.entities.*;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioNotificaciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PerfilServiceImplTest {

  @Mock
  private RepositorioPerfiles repositorioPerfiles;
  @Mock
  private RepositorioPropuestas repositorioPropuestas;
  @Mock
  private RepositorioSubastas repositorioSubastas;
  @Mock
  private RepositorioNotificaciones repositorioNotificaciones;
  @Mock
  private RepositorioFiguritasIntercambiables repositorioFiguritasIntercambiables;

  private PerfilService service;
  private Perfil usuario;
  private Perfil otro;

  @BeforeEach
  void setUp() {
    service = new PerfilService(repositorioPerfiles, repositorioPropuestas,
        repositorioSubastas, repositorioFiguritasIntercambiables, repositorioNotificaciones);

    usuario = new Perfil("u-1", new Usuario("usr-1", Rol.USUARIO), "Lucas",
        new Coleccion(), telegram("@lucas"), new ArrayList<>());
    otro = new Perfil("u-2", new Usuario("usr-2", Rol.USUARIO), "Sofía",
        new Coleccion(), telegram("@sofia"), new ArrayList<>());
  }

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  private Propuesta propuesta(String id, Perfil autor, Perfil destino, EstadoProceso estado) {
    return new Propuesta(id, autor, destino, new ArrayList<>(), null,
        new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), estado))));
  }

  @Test
  void getOperacionesUsuario_usuarioInexistente_retornaNull() {
    when(repositorioPerfiles.buscarPorId("u-99")).thenReturn(null);

    assertNull(service.obtenerOperacionesPerfil("u-99"));
  }

  @Test
  void getOperacionesUsuario_usuarioExistente_retornaOperaciones() {
    usuario.getColeccion().getRepetidas().add(new FiguritaIntercambiable(null, 1, List.of(MetodoIntercambio.INTERCAMBIO)));

    Propuesta oferta = propuesta("p-3", otro, usuario, EstadoProceso.ACEPTADO);
    Subasta subastaActiva = new Subasta("s-1", usuario,
        LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(2), null);
    subastaActiva.getOfertas().add(oferta);

    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPropuestas.buscarPorAutorId("u-1")).thenReturn(
        List.of(propuesta("p-1", usuario, otro, EstadoProceso.PENDIENTE)));
    when(repositorioPropuestas.buscarPorDestinatarioId("u-1")).thenReturn(
        List.of(propuesta("p-2", otro, usuario, EstadoProceso.RECHAZADO)));
    when(repositorioSubastas.buscarPorPerfilId("u-1")).thenReturn(List.of(subastaActiva));

    OperacionesDto resultado = service.obtenerOperacionesPerfil("u-1");

    assertEquals(1, resultado.getFiguritasPublicadas().size());
    assertEquals(1, resultado.getPropuestasEnviadas().size());
    assertEquals(1, resultado.getPropuestasRecibidas().size());
    assertEquals(1, resultado.getSubastasActivas().size());
  }

  @Test
  void getOperacionesUsuario_filtraSoloSubastasActivas() {
    Propuesta oferta = propuesta("p-1", otro, usuario, EstadoProceso.ACEPTADO);
    Subasta subastaActiva = new Subasta("s-1", usuario,
        LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(2), null);
    subastaActiva.getOfertas().add(oferta);

    Subasta subastaVencida = new Subasta("s-2", usuario,
        LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1), null);

    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPropuestas.buscarPorAutorId("u-1")).thenReturn(new ArrayList<>());
    when(repositorioPropuestas.buscarPorDestinatarioId("u-1")).thenReturn(new ArrayList<>());
    when(repositorioSubastas.buscarPorPerfilId("u-1")).thenReturn(List.of(subastaActiva, subastaVencida));

    OperacionesDto resultado = service.obtenerOperacionesPerfil("u-1");

    assertEquals(1, resultado.getSubastasActivas().size());
    assertEquals("s-1", resultado.getSubastasActivas().get(0).getId());
  }

  @Test
  void getIntercambiablesUsuario_usuarioExistente_retornaLista() {
    Figurita figurita = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
    FiguritaIntercambiable fi = new FiguritaIntercambiable(figurita, 2, List.of(MetodoIntercambio.INTERCAMBIO));

    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioFiguritasIntercambiables.buscarPorUsuarioId("u-1")).thenReturn(List.of(fi));

    List<FiguritaIntercambiableDto> resultado = service.obtenerIntercambiablesPerfil("u-1");

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getFiguritaId());
  }

  @Test
  void getIntercambiablesUsuario_usuarioInexistente_lanzaNotFoundException() {
    when(repositorioPerfiles.buscarPorId("u-99")).thenReturn(null);

    assertThrows(NotFoundException.class,
        () -> service.obtenerIntercambiablesPerfil("u-99"));
  }

  @Test
  void agregarCalificacion_valida_retornaPromedio() {
    usuario.getCalificaciones().add(new Calificacion("c-0", otro, 4, "Buen intercambio"));

    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorId("u-2")).thenReturn(otro);

    CalificacionDto resultado = service.agregarCalificacion("u-2", "u-1", 2, "Tardó en responder");

    assertEquals(3.0f, resultado.getCalificacionFinal().floatValue());
    verify(repositorioPerfiles).guardar(usuario);
  }

  @Test
  void agregarCalificacion_valorNegativo_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("u-2", "u-1", -1, "Malo"));
  }

  @Test
  void agregarCalificacion_valorCero_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("u-2", "u-1", 0, "Malo"));
  }

  @Test
  void agregarCalificacion_valorMayorACinco_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("u-2", "u-1", 6, "Excelente"));
  }

  @Test
  void agregarCalificacion_valorNulo_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("u-2", "u-1", null, "Sin valor"));
  }

  @Test
  void agregarCalificacion_valorLimiteMinimo_noLanzaExcepcion() {
    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorId("u-2")).thenReturn(otro);

    CalificacionDto resultado = service.agregarCalificacion("u-2", "u-1", 1, "Muy malo");

    assertEquals(1.0f, resultado.getCalificacionFinal().floatValue());
  }

  @Test
  void agregarCalificacion_valorLimiteMaximo_noLanzaExcepcion() {
    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorId("u-2")).thenReturn(otro);

    CalificacionDto resultado = service.agregarCalificacion("u-2", "u-1", 5, "Excelente");

    assertEquals(5.0f, resultado.getCalificacionFinal().floatValue());
  }

  @Test
  void obtenerSugerencias_conCoincidencias_retornaSugerencias() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
    Figurita diMaria   = new Figurita("ARG-11", 11, "Di María",  Seleccion.ARGENTINA);
    usuario.getColeccion().getFaltantes().add(messi);
    usuario.getColeccion().getRepetidas().add(new FiguritaIntercambiable(diMaria, 2, new ArrayList<>()));

    Coleccion coleccionOtro = new Coleccion();
    coleccionOtro.getRepetidas().add(new FiguritaIntercambiable(messi, 2, new ArrayList<>()));
    coleccionOtro.getFaltantes().add(diMaria);
    Perfil otroConMessi = new Perfil("u-3", new Usuario("usr-3", Rol.USUARIO), "Juan",
        coleccionOtro, telegram("@juan"), new ArrayList<>());

    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarTodos()).thenReturn(List.of(usuario, otroConMessi));

    var resultado = service.obtenerSugerencias("u-1");

    assertEquals(1, resultado.size());
  }

  @Test
  void obtenerSugerencias_sinCoincidencias_retornaListaVacia() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
    usuario.getColeccion().getFaltantes().add(messi);

    when(repositorioPerfiles.buscarPorId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarTodos()).thenReturn(List.of(usuario, otro));

    var resultado = service.obtenerSugerencias("u-1");

    assertEquals(0, resultado.size());
  }
}