package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.dto.FiguritaIntercambiableDto;
import app.dto.OperacionesDto;
import app.dto.filtros.SugerenciasFiltro;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.model.entities.*;
import app.repositories.RepositorioCalificacion;
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
class ServicioPerfilImplTest {

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
  @Mock
  private RepositorioCalificacion repositorioCalificacion;

  private ServicioPerfil service;
  private Perfil usuario;
  private Perfil otro;

  @BeforeEach
  void setUp() {
    service = new ServicioPerfil(repositorioCalificacion, repositorioPerfiles, repositorioPropuestas,
        repositorioSubastas, repositorioFiguritasIntercambiables, repositorioNotificaciones);

    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    usuario = Perfil.builder()
        .id("1").usuario(user).nombre("Lucas")
        .mediosDeContacto(telegram("@lucas"))
        .build();

    user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    otro = Perfil.builder()
        .id("otro").usuario(user).nombre("Sofía")
        .mediosDeContacto(telegram("@sofía"))
        .build();
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

    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPropuestas.buscarPorAutorId("1")).thenReturn(
        List.of(propuesta("p-1", usuario, otro, EstadoProceso.PENDIENTE)));
    when(repositorioPropuestas.buscarPorDestinatarioId("1")).thenReturn(
        List.of(propuesta("p-2", otro, usuario, EstadoProceso.RECHAZADO)));
    when(repositorioSubastas.buscarPorAutorUserId("1")).thenReturn(List.of(subastaActiva));

    OperacionesDto resultado = service.obtenerOperacionesPerfil("1");

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

    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPropuestas.buscarPorAutorId("1")).thenReturn(new ArrayList<>());
    when(repositorioPropuestas.buscarPorDestinatarioId("1")).thenReturn(new ArrayList<>());
    when(repositorioSubastas.buscarPorAutorUserId("1")).thenReturn(List.of(subastaActiva, subastaVencida));

    OperacionesDto resultado = service.obtenerOperacionesPerfil("1");

    assertEquals(1, resultado.getSubastasActivas().size());
    assertEquals("s-1", resultado.getSubastasActivas().get(0).getId());
  }

  @Test
  void getIntercambiablesUsuario_usuarioExistente_retornaLista() {
    Figurita figurita = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    FiguritaIntercambiable fi = new FiguritaIntercambiable(figurita, 2, new ArrayList<>());

    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioFiguritasIntercambiables.buscarPorUsuarioId("1")).thenReturn(List.of(fi));

    List<FiguritaIntercambiableDto> resultado = service.obtenerIntercambiablesPerfil("1");

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
  void agregarCalificacion_valida_guardaCalificacion() {
    usuario.getCalificaciones().add(new Calificacion("c-0", otro, 4, "Buen intercambio", "t-0", MetodoIntercambio.INTERCAMBIO));

    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(otro);

    service.agregarCalificacion("u-2", "1", 2, "Tardó en responder", "t-1", MetodoIntercambio.INTERCAMBIO);

    verify(repositorioPerfiles).guardar(usuario);
  }

  @Test
  void agregarCalificacion_yaCalificado_lanzaExcepcion() {
    Calificacion existente = new Calificacion("c-0", otro, 4, "Buen intercambio", "t-1", MetodoIntercambio.INTERCAMBIO);
    usuario.getCalificaciones().add(existente);

    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(otro);

    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("u-2", "1", 3, "Otra vez", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorNegativo_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("2", "1", -1, "Malo", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorCero_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("2", "1", 0, "Malo", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorMayorACinco_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("2", "1", 6, "Excelente", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorNulo_lanzaExcepcion() {
    assertThrows(BadRequestException.class,
        () -> service.agregarCalificacion("2", "1", null, "Sin valor", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorLimiteMinimo_noLanzaExcepcion() {
    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(otro);

    assertDoesNotThrow(() -> service.agregarCalificacion("u-2", "1", 1, "Muy malo", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorLimiteMaximo_noLanzaExcepcion() {
    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorUsuarioId("u-2")).thenReturn(otro);

    assertDoesNotThrow(() -> service.agregarCalificacion("u-2", "1", 5, "Excelente", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void obtenerSugerencias_conCoincidencias_retornaSugerencias() {
    Figurita messi   = new Figurita("ARG-10", 10, "Messi",   Seleccion.ARGENTINA, null);
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);
    usuario.getColeccion().getFaltantes().add(messi);
    usuario.getColeccion().getRepetidas().add(new FiguritaIntercambiable(diMaria, 2, new ArrayList<>()));

    Coleccion coleccionOtro = new Coleccion();
    coleccionOtro.getRepetidas().add(new FiguritaIntercambiable(messi, 2, new ArrayList<>()));
    coleccionOtro.getFaltantes().add(diMaria);

    Usuario user = new Usuario("usr-3", Rol.USUARIO, "lucas", "fiscella");
    Perfil otroConMessi = Perfil.builder()
        .id("3").usuario(user).nombre("Juan")
        .coleccion(coleccionOtro)
        .mediosDeContacto(telegram("@juan"))
        .build();

    when(repositorioPerfiles.buscarPorUsuarioId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarTodos()).thenReturn(List.of(usuario, otroConMessi));

    var resultado = service.obtenerSugerencias("u-1", new SugerenciasFiltro(null, 1, 10));

    assertEquals(1, resultado.data().size());
  }

  @Test
  void obtenerSugerencias_sinCoincidencias_retornaListaVacia() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    usuario.getColeccion().getFaltantes().add(messi);

    when(repositorioPerfiles.buscarPorUsuarioId("u-1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarTodos()).thenReturn(List.of(usuario, otro));

    var resultado = service.obtenerSugerencias("u-1", new SugerenciasFiltro(null, 1, 10));

    assertEquals(0, resultado.data().size());
  }

  @Test
  void obtenerFaltantes_usuarioExistente_retornaLista() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    usuario.getColeccion().getFaltantes().add(messi);

    when(repositorioPerfiles.buscarPorUsuarioId("1")).thenReturn(usuario);

    var resultado = service.obtenerFaltantes("1");

    assertEquals(1, resultado.size());
  }

  @Test
  void obtenerFaltantes_usuarioInexistente_lanzaNotFoundException() {
    when(repositorioPerfiles.buscarPorUsuarioId("u-99"))
        .thenThrow(new NotFoundException("Perfil no encontrado para el usuario: u-99"));

    assertThrows(NotFoundException.class,
        () -> service.obtenerFaltantes("u-99"));
  }

  @Test
  void obtenerRepetidas_usuarioInexistente_lanzaNotFoundException() {
    when(repositorioPerfiles.buscarPorUsuarioId("u-99"))
        .thenThrow(new NotFoundException("Perfil no encontrado para el usuario: u-99"));

    assertThrows(NotFoundException.class,
        () -> service.obtenerRepetidas("u-99"));
  }

  @Test
  void obtenerRepetidas_usuarioExistente_retornaLista() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    usuario.getColeccion().getRepetidas().add(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.INTERCAMBIO)));

    when(repositorioPerfiles.buscarPorUsuarioId("1")).thenReturn(usuario);

    var resultado = service.obtenerRepetidas("1");

    assertEquals(1, resultado.size());
  }

}