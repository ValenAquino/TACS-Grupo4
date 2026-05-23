package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.MongoTestBase;
import app.dto.request.MejorarOfertaRequest;
import app.exceptions.BadRequestException;
import app.model.entities.*;
import java.time.LocalDateTime;
import java.util.List;
import app.servicios.ServicioSubasta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicioSubastaTest extends MongoTestBase {

  @Autowired
  private ServicioSubasta service;

  private Perfil lucas;
  private Perfil sofia;
  private Figurita messi;

  private List<MedioDeContacto> telegram(String numero) {
    return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
  }

  @BeforeEach
  void setUp() {
    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
    repositorioFiguritas.guardar( messi);

    Coleccion coleccionSinMessi = new Coleccion("c-1");
    coleccionSinMessi.getFaltantes().add(messi);

    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    lucas = Perfil.builder()
        .id("1").usuario(user).nombre("Lucas")
        .coleccion(coleccionSinMessi)
        .mediosDeContacto(telegram("@lucas"))
        .build();

    repositorioUsuarios.guardar(user);
    repositorioColecciones.guardar(coleccionSinMessi);
    repositorioPerfiles.guardar(lucas);

    Coleccion coleccionRepetidos = new Coleccion("c-2");
    coleccionRepetidos.getRepetidas().add(new FiguritaIntercambiable(messi, 1, List.of(MetodoIntercambio.INTERCAMBIO)));

    user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    sofia = Perfil.builder()
        .id("2").usuario(user).nombre("Sofía")
        .coleccion(coleccionRepetidos)
        .mediosDeContacto(telegram("@sofia"))
        .build();

    repositorioUsuarios.guardar(user);
    repositorioColecciones.guardar(coleccionRepetidos);
    repositorioPerfiles.guardar(sofia);
  }

  @Test
  void crearSubastaNotificaUsuarios() {

    service.crearSubasta("u-2", "ARG-10", 30, List.of(), 0);
    assertEquals(1, repositorioNotificaciones.buscarPorPerfil(lucas).size());
  }

  @Test
  void crearSubastaNoNotificaUsuarios() {
    Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);

    repositorioFiguritas.guardar(diMaria);

    service.crearSubasta("u-2", "ARG-11", 30, List.of(), 0);
    assertEquals(0, repositorioNotificaciones.buscarPorPerfil(lucas).size());
  }

  @Test
  void ofertarEnSubastaCerrada_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(3))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.ofertarEnSubasta("2", "s-1", List.of("ARG-10")));
  }

  @Test
  void ofertarEnSubastaConFiguritasDuplicadas_lanzaExcepcion() {
    Subasta subastaActiva = Subasta.builder()
        .id("s-2")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subastaActiva);

    assertThrows(BadRequestException.class,
        () -> service.ofertarEnSubasta("2","s-2", List.of("ARG-10", "ARG-10")));
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
        .build();
    subasta.agregarOferta(propuesta);

    repositorioSubastas.guardar(subasta);

    service.seleccionarOferta("s-1", "o-1");

    subasta = repositorioSubastas.buscarPorId("s-1");

    assertEquals(EstadoProceso.SELECCIONADO, buscarOfertaEn(subasta,propuesta.getId()).getEstadoActual().getValor());
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
    subasta.agregarOferta(propuestaAnterior);
    subasta.agregarOferta(propuestaNueva);

    repositorioSubastas.guardar(subasta);

    service.seleccionarOferta("s-1", "o-2");

    subasta = repositorioSubastas.buscarPorId("s-1");

    assertEquals(EstadoProceso.PENDIENTE, buscarOfertaEn(subasta,propuestaAnterior.getId()).getEstadoActual().getValor());
    assertEquals(EstadoProceso.SELECCIONADO,  buscarOfertaEn(subasta,propuestaNueva.getId()).getEstadoActual().getValor());
  }

  @Test
  void seleccionarOferta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

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
    subasta.agregarOferta(propuesta);

    repositorioSubastas.guardar(subasta);

    service.rechazarOferta("s-1", "o-1");

    subasta = repositorioSubastas.buscarPorId("s-1");

    assertEquals(EstadoProceso.RECHAZADO, buscarOfertaEn(subasta, propuesta.getId()).getEstadoActual().getValor());
  }

  @Test
  void rechazarOferta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

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
    subasta.agregarOferta(propuesta1);
    subasta.agregarOferta(propuesta2);

    repositorioSubastas.guardar(subasta);

    service.cancelarSubasta("s-1");

    subasta = repositorioSubastas.buscarPorId("s-1");

    assertEquals(EstadoProceso.RECHAZADO, buscarOfertaEn(subasta, propuesta1.getId()).getEstadoActual().getValor());
    assertEquals(EstadoProceso.RECHAZADO, buscarOfertaEn(subasta, propuesta2.getId()).getEstadoActual().getValor());
    assertTrue(subasta.getFechaCierre().isBefore(LocalDateTime.now().plusSeconds(1)));
  }

  @Test
  void cancelarSubasta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

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

    propuestaSeleccionada.seleccionar(sofia.getId());

    Propuesta propuestaPendiente = Propuesta.builder()
        .id("o-2").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();

    subasta.agregarOferta(propuestaSeleccionada);
    subasta.agregarOferta(propuestaPendiente);

    repositorioSubastas.guardar(subasta);

    service.cerrarSubasta("s-1");

    subasta = repositorioSubastas.buscarPorId("s-1");

    assertEquals(EstadoProceso.ACEPTADO, buscarOfertaEn(subasta, propuestaSeleccionada.getId()).getEstadoActual().getValor());
    assertEquals(EstadoProceso.RECHAZADO, buscarOfertaEn(subasta, propuestaPendiente.getId()).getEstadoActual().getValor());
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
    subasta.agregarOferta(propuesta);

    repositorioSubastas.guardar(subasta);

    assertThrows(BadRequestException.class,
        () -> service.cerrarSubasta("s-1"));
  }

  @Test
  void cerrarSubasta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder().id("s-1").autor(sofia).fechaInicio(
            LocalDateTime.now().minusDays(2)).fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.cerrarSubasta("s-1"));
  }

  @Test
  void ofertarEnSubasta_agregaOfertaCorrectamente() {

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(
            LocalDateTime.now().minusHours(1)
        )
        .fechaCierre(
            LocalDateTime.now().plusDays(1)
        )
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    service.ofertarEnSubasta(
        "1",
        "s-1",
        List.of("ARG-10")
    );

    subasta =
        repositorioSubastas.buscarPorId(
            "s-1"
        );

    assertEquals(
        1,
        subasta.getOfertas().size()
    );

    assertEquals(
        lucas.getId(),
        subasta.getOfertas()
            .get(0)
            .getAutor()
            .getId()
    );
  }

  @Test
  void mejorarOferta_actualizaFiguritas() {

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(
            LocalDateTime.now().minusHours(1)
        )
        .fechaCierre(
            LocalDateTime.now().plusDays(1)
        )
        .figuritaSubastada(messi)
        .build();

    Propuesta propuesta =
        Propuesta.builder()
            .id("o-1")
            .autor(lucas)
            .destinatario(sofia)
            .figuritaBuscada(messi)
            .figuritasOfrecidas(List.of())
            .build();

    subasta.agregarOferta(propuesta);

    repositorioSubastas.guardar(subasta);

    service.mejorarOfertaEnSubasta(
        "s-1",
        "o-1",
        new MejorarOfertaRequest(
            List.of("ARG-10")
        )
    );

    subasta =
        repositorioSubastas.buscarPorId(
            "s-1"
        );

    assertEquals(
        1,
        buscarOfertaEn(
            subasta,
            "o-1"
        )
            .getFiguritasOfrecidas()
            .size()
    );
  }

  @Test
  void mejorarOferta_ofertaInexistente_lanzaExcepcion() {

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(
            LocalDateTime.now().minusHours(1)
        )
        .fechaCierre(
            LocalDateTime.now().plusDays(1)
        )
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    assertThrows(
        BadRequestException.class,
        () -> service.mejorarOfertaEnSubasta(
            "s-1",
            "inexistente",
            new MejorarOfertaRequest(
                List.of("ARG-10")
            )
        )
    );
  }

  @Test
  void seleccionarOferta_ofertaInexistente_lanzaExcepcion() {

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(
            LocalDateTime.now().minusHours(1)
        )
        .fechaCierre(
            LocalDateTime.now().plusDays(1)
        )
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    assertThrows(
        BadRequestException.class,
        () -> service.seleccionarOferta(
            "s-1",
            "inexistente"
        )
    );
  }

  @Test
  void rechazarOferta_ofertaInexistente_lanzaExcepcion() {

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(
            LocalDateTime.now().minusHours(1)
        )
        .fechaCierre(
            LocalDateTime.now().plusDays(1)
        )
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    assertThrows(
        BadRequestException.class,
        () -> service.rechazarOferta(
            "s-1",
            "inexistente"
        )
    );
  }

  private Propuesta buscarOfertaEn(Subasta subasta, String ofertaId) {
    return subasta.getOfertas().stream()
        .filter(o -> o.getId().equals(ofertaId))
        .findFirst()
        .orElseThrow();
  }
}