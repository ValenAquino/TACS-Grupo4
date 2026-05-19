package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import app.MongoTestBase;
import app.dto.FiguritaIntercambiableDto;
import app.dto.OperacionesDto;
import app.dto.filtros.SugerenciasFiltro;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.model.entities.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import app.servicios.ServicioPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ServicioPerfilTest extends MongoTestBase {

  private ServicioPerfil service;
  private Perfil usuario;
  private Perfil otro;

  private Figurita messi;

  @BeforeEach
  void setUp() {
    service = new ServicioPerfil(repositorioCalificacion, repositorioPerfiles, repositorioPropuestas,
        repositorioSubastas, repositorioFiguritasIntercambiables, repositorioNotificaciones);

    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    Coleccion colec = new Coleccion("c-1");
    usuario = Perfil.builder()
        .id("1").usuario(user).nombre("Lucas")
        .coleccion(colec)
        .mediosDeContacto(telegram("@lucas"))
        .build();
    repositorioColecciones.guardar(colec);
    repositorioUsuarios.guardar(user);

    user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    colec = new Coleccion("c-2");
    otro = Perfil.builder()
        .id("otro").usuario(user).nombre("Sofía")
        .coleccion(colec)
        .mediosDeContacto(telegram("@sofía"))
        .build();
    repositorioColecciones.guardar(colec);
    repositorioUsuarios.guardar(user);
    repositorioPerfiles.guardar(usuario);
    repositorioPerfiles.guardar(otro);

    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    repositorioFiguritas.guardar(messi);
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

    assertThrows(NotFoundException.class, ()->service.obtenerOperacionesPerfil("u-99"));
  }

  @Test
  void getOperacionesUsuario_usuarioExistente_retornaOperaciones() {
    usuario.getColeccion().getRepetidas().add(new FiguritaIntercambiable(messi, 1, List.of(MetodoIntercambio.INTERCAMBIO)));
    repositorioColecciones.guardar(usuario.getColeccion());

    Propuesta oferta = propuesta("p-3", otro, usuario, EstadoProceso.ACEPTADO);
    repositorioPropuestas.guardar(oferta);

    Subasta subastaActiva = Subasta.builder().id("s-1").autor(usuario).fechaInicio(
        LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(2)).build();
    subastaActiva.getOfertas().add(oferta);
    repositorioSubastas.guardar(subastaActiva);

    OperacionesDto resultado = service.obtenerOperacionesPerfil("1");

    assertEquals(1, resultado.getFiguritasPublicadas().size());
    assertEquals(1, resultado.getPropuestasEnviadas().size());
    assertEquals(1, resultado.getPropuestasRecibidas().size());
    assertEquals(1, resultado.getSubastasActivas().size());
  }

  @Test
  void getOperacionesUsuario_filtraSoloSubastasActivas() {
    Propuesta oferta = propuesta("p-1", otro, usuario, EstadoProceso.ACEPTADO);
    repositorioPropuestas.guardar(oferta);
    Subasta subastaActiva = Subasta.builder().id("s-1").autor(usuario).fechaInicio(
        LocalDateTime.now().minusHours(1)).fechaCierre(LocalDateTime.now().plusDays(2)).build();
    subastaActiva.getOfertas().add(oferta);
    repositorioSubastas.guardar(subastaActiva);

    Subasta subastaVencida = Subasta.builder().id("s-2").autor(usuario).fechaInicio(
        LocalDateTime.now().minusDays(3)).fechaCierre(LocalDateTime.now().minusDays(1)).build();
    repositorioSubastas.guardar(subastaVencida);

    OperacionesDto resultado = service.obtenerOperacionesPerfil("1");

    assertEquals(1, resultado.getSubastasActivas().size());
    assertEquals("s-1", resultado.getSubastasActivas().get(0).getId());
  }

  @Test
  void getIntercambiablesUsuario_usuarioExistente_retornaLista() {
    FiguritaIntercambiable fi = new FiguritaIntercambiable(messi, 2, new ArrayList<>());
    usuario.getColeccion().agregarRepetida(fi);
    repositorioColecciones.guardar(usuario.getColeccion());
    repositorioPerfiles.guardar(usuario);

    List<FiguritaIntercambiableDto> resultado = service.obtenerIntercambiablesPerfil("1");

    assertEquals(1, resultado.size());
    assertEquals("ARG-10", resultado.get(0).getFiguritaId());
  }

  @Test
  void getIntercambiablesUsuario_usuarioInexistente_lanzaNotFoundException() {

    assertThrows(NotFoundException.class,
        () -> service.obtenerIntercambiablesPerfil("u-99"));
  }

  @Test
  void agregarCalificacion_valida_guardaCalificacion() {
    Calificacion calificacion = new Calificacion("c-0", usuario, 4, "Buen intercambio", "t-1", MetodoIntercambio.INTERCAMBIO);
    repositorioCalificacion.guardar(calificacion);
    usuario.getCalificaciones().add(calificacion);
    repositorioPerfiles.guardar(usuario);

    service.agregarCalificacion("u-2", "1", 2, "Tardó en responder", "t-1", MetodoIntercambio.INTERCAMBIO);

    assertEquals(repositorioPerfiles.buscarPorId("1").getCalificacionMedia(), 4);
  }

  @Test
  void agregarCalificacion_yaCalificado_lanzaExcepcion() {
    Calificacion existente = new Calificacion("c-0", otro, 4, "Buen intercambio", "t-1", MetodoIntercambio.INTERCAMBIO);
    usuario.getCalificaciones().add(existente);

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

    assertDoesNotThrow(() -> service.agregarCalificacion("u-2", "1", 1, "Muy malo", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void agregarCalificacion_valorLimiteMaximo_noLanzaExcepcion() {

    assertDoesNotThrow(() -> service.agregarCalificacion("u-2", "1", 5, "Excelente", "t-1", MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void obtenerSugerencias_conCoincidencias_retornaSugerencias() {
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);
    repositorioFiguritas.guardar(diMaria);
    usuario.getColeccion().agregarFaltante(messi);
    usuario.getColeccion().getRepetidas().add(new FiguritaIntercambiable(diMaria, 2, new ArrayList<>()));
    repositorioPerfiles.guardar(usuario);

    Coleccion coleccionOtro = new Coleccion("c-3");
    coleccionOtro.getRepetidas().add(new FiguritaIntercambiable(messi, 2, new ArrayList<>()));
    coleccionOtro.getFaltantes().add(diMaria);

    repositorioColecciones.guardar(coleccionOtro);

    Usuario user = new Usuario("u-3", Rol.USUARIO, "lucas", "fiscella");
    repositorioUsuarios.guardar(user);
    Perfil otroConMessi = Perfil.builder()
        .id("3").usuario(user).nombre("Juan")
        .coleccion(coleccionOtro)
        .mediosDeContacto(telegram("@juan"))
        .build();
    repositorioPerfiles.guardar(otroConMessi);

    var resultado = service.obtenerSugerencias("u-1", new SugerenciasFiltro(null, 1, 10));

    assertEquals(1, resultado.contenido().size());
  }

  @Test
  void obtenerSugerencias_sinCoincidencias_retornaListaVacia() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    usuario.getColeccion().getFaltantes().add(messi);

    var resultado = service.obtenerSugerencias("u-1", new SugerenciasFiltro(null, 1, 10));

    assertEquals(0, resultado.contenido().size());
  }

  @Test
  void obtenerFaltantes_usuarioExistente_retornaLista() {

    usuario.getColeccion().agregarFaltante(messi);
    repositorioPerfiles.guardar(usuario);

    var resultado = service.obtenerFaltantes("u-1");

    assertEquals(1, resultado.size());
  }

  @Test
  void obtenerFaltantes_usuarioInexistente_lanzaNotFoundException() {

    assertThrows(NotFoundException.class,
        () -> service.obtenerFaltantes("u-99"));
  }

  @Test
  void obtenerRepetidas_usuarioInexistente_lanzaNotFoundException() {

    assertThrows(NotFoundException.class,
        () -> service.obtenerRepetidas("u-99"));
  }

  @Test
  void obtenerRepetidas_usuarioExistente_retornaLista() {
    usuario.getColeccion().getRepetidas().add(
        new FiguritaIntercambiable(messi, 2, List.of(MetodoIntercambio.INTERCAMBIO)));

    repositorioPerfiles.guardar(usuario);

    var resultado = service.obtenerRepetidas("u-1");

    assertEquals(1, resultado.size());
  }

}