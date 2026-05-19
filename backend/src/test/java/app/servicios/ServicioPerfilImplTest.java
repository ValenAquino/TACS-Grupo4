package app.servicios;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.dto.FiguritaIntercambiableDto;
import app.dto.filtros.SugerenciasFiltro;
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

  private ServicioPerfil service;
  private Perfil usuario;
  private Perfil otro;

  @BeforeEach
  void setUp() {
    service = new ServicioPerfil(repositorioPerfiles, repositorioPropuestas,
        repositorioSubastas, repositorioFiguritasIntercambiables, repositorioNotificaciones);

    usuario = new Perfil("1", new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella"), "Lucas",
        new Coleccion(), telegram("@lucas"), new ArrayList<>());
    otro = new Perfil("2", new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella"), "Sofía",
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
    usuario.getCalificaciones().add(
        new Calificacion(
            "c-0",
            otro,
            4,
            "Buen intercambio",
            "t-0",
            MetodoIntercambio.INTERCAMBIO
        )
    );

    when(repositorioPerfiles.buscarPorId("1"))
        .thenReturn(usuario);

    when(repositorioPerfiles.buscarPorId("u-2"))
        .thenReturn(otro);

    service.agregarCalificacion(
        "u-2",
        "1",
        2,
        "Tardó en responder",
        "t-1",
        MetodoIntercambio.INTERCAMBIO
    );

    verify(repositorioPerfiles).guardar(usuario);
  }

  @Test
  void agregarCalificacion_yaCalificado_lanzaExcepcion() {
    Calificacion existente = new Calificacion("c-0", otro, 4, "Buen intercambio", "t-1", MetodoIntercambio.INTERCAMBIO);
    usuario.getCalificaciones().add(existente);

    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorId("u-2")).thenReturn(otro);

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
    when(repositorioPerfiles.buscarPorId("u-2")).thenReturn(otro);

    assertDoesNotThrow(() -> service.agregarCalificacion("u-2", "1", 1, "Muy malo", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorLimiteMaximo_noLanzaExcepcion() {
    when(repositorioPerfiles.buscarPorId("1")).thenReturn(usuario);
    when(repositorioPerfiles.buscarPorId("u-2")).thenReturn(otro);

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
    Perfil otroConMessi = new Perfil("u-3", new Usuario("usr-3", Rol.USUARIO, "lucas", "fiscella"), "Juan",
        coleccionOtro, telegram("@juan"), new ArrayList<>());

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