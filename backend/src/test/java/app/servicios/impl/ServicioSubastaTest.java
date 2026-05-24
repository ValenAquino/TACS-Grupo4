package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.MongoTestBase;
import app.dto.filtros.SubastasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.EditarOfertaRequest;
import app.dto.subasta.MiSubastaActivaDto;
import app.dto.subasta.MiSubastaFinalizadaDto;
import app.dto.subasta.SubastaParticipoDto;

import app.exceptions.BadRequestException;
import app.model.entities.*;
import java.time.LocalDateTime;
import java.util.List;

import app.repositories.impl.campos.CamposSubasta;
import app.servicios.ServicioSubasta;
import org.bson.types.ObjectId;
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
    repositorioFiguritas.guardar(messi);

    Coleccion coleccionSinMessi = new Coleccion("c-1");
    coleccionSinMessi.getFaltantes().add(messi);

    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    lucas = Perfil.builder()
        .id(new ObjectId().toString()).usuario(user).nombre("Lucas")
        .coleccion(coleccionSinMessi)
        .mediosDeContacto(telegram("@lucas"))
        .build();

    repositorioUsuarios.guardar(user);
    repositorioColecciones.guardar(coleccionSinMessi);
    repositorioPerfiles.guardar(lucas);

    Coleccion coleccionRepetidos = new Coleccion("c-2");
    coleccionRepetidos.getRepetidas().add(new FiguritaIntercambiable(messi, 1, List.of(MetodoIntercambio.INTERCAMBIO)));

    user = new Usuario("u-2", Rol.USUARIO, "sofia", "ape");
    sofia = Perfil.builder()
        .id(new ObjectId().toString()).usuario(user).nombre("Sofía")
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
        () -> service.ofertarEnSubasta(sofia.getId(), "s-1", List.of("ARG-10")));
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
        () -> service.ofertarEnSubasta(sofia.getId(), "s-2", List.of("ARG-10", "ARG-10")));
  }

  @Test
  void seleccionarOferta_marcaComoSeleccionada() {
    Subasta subasta = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuesta = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    subasta.agregarOferta(propuesta);

    repositorioSubastas.guardar(subasta);

    service.seleccionarOferta(sofia.getId(), "s-1", "o-1");

    subasta = repositorioSubastas.buscarPorId("s-1", new CamposSubasta(true, true));

    assertEquals(EstadoProceso.SELECCIONADO, buscarOfertaEn(subasta, propuesta.getId()).getEstadoActual().getValor());
  }

  @Test
  void seleccionarOferta_desseleccionaAnterior() {
    Subasta subasta = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    Propuesta propuestaAnterior = Propuesta.builder()
        .id("o-1").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();
    propuestaAnterior.seleccionar(sofia.getId());

    Propuesta propuestaNueva = Propuesta.builder()
        .id("o-2").autor(lucas)
        .destinatario(sofia)
        .figuritasOfrecidas(List.of())
        .figuritaBuscada(messi)
        .build();

    subasta.agregarOferta(propuestaAnterior);
    subasta.agregarOferta(propuestaNueva);

    repositorioSubastas.guardar(subasta);

    service.seleccionarOferta(sofia.getId(), "s-1", "o-2");

    subasta = repositorioSubastas.buscarPorId("s-1", new CamposSubasta(true, true));

    assertEquals(EstadoProceso.PENDIENTE, buscarOfertaEn(subasta, propuestaAnterior.getId()).getEstadoActual().getValor());
    assertEquals(EstadoProceso.SELECCIONADO, buscarOfertaEn(subasta, propuestaNueva.getId()).getEstadoActual().getValor());
  }

  @Test
  void seleccionarOferta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(2))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.seleccionarOferta(sofia.getId(), "s-1", "o-1"));
  }

  @Test
  void seleccionarOferta_ofertaInexistente_lanzaExcepcion() {
    Subasta subasta = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subasta);

    assertThrows(BadRequestException.class,
        () -> service.seleccionarOferta(sofia.getId(), "s-1", "inexistente"));
  }

  @Test
  void rechazarOferta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(2))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.rechazarOferta(sofia.getId(), "s-1", "o-1"));
  }

  @Test
  void rechazarOferta_ofertaInexistente_lanzaExcepcion() {
    Subasta subasta = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subasta);

    assertThrows(BadRequestException.class,
        () -> service.rechazarOferta(sofia.getId(), "s-1", "inexistente"));
  }

  @Test
  void cancelarSubasta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(2))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.cancelarSubasta(sofia.getId(), "s-1"));
  }

  @Test
  void cerrarSubasta_subastaInactiva_lanzaExcepcion() {
    Subasta subastaCerrada = Subasta.builder()
        .id("s-1").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(2))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi).build();

    repositorioSubastas.guardar(subastaCerrada);

    assertThrows(BadRequestException.class,
        () -> service.cerrarSubasta(sofia.getId(), "s-1"));
  }

  @Test
  void ofertarEnSubasta_agregaOfertaCorrectamente() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    service.ofertarEnSubasta(lucas.getId(), "s-1", List.of("ARG-10"));

    subasta = repositorioSubastas.buscarPorId("s-1", new CamposSubasta(true, true));

    assertEquals(1, subasta.getOfertas().size());
    assertEquals(lucas.getId(), subasta.getOfertas().get(0).getAutor().getId());
  }

  @Test
  void editarOferta_actualizaFiguritas() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    Propuesta propuesta = Propuesta.builder()
        .id("o-1")
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of())
        .build();

    subasta.agregarOferta(propuesta);
    repositorioSubastas.guardar(subasta);

    service.editarOfertaEnSubasta(lucas.getId(), "s-1", "o-1", new EditarOfertaRequest(List.of("ARG-10")));

    subasta = repositorioSubastas.buscarPorId("s-1", new CamposSubasta(true, true));

    assertEquals(1, buscarOfertaEn(subasta, "o-1").getFiguritasOfrecidas().size());
  }

  @Test
  void editarOferta_ofertaInexistente_lanzaExcepcion() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    assertThrows(BadRequestException.class,
        () -> service.editarOfertaEnSubasta(lucas.getId(), "s-1", "inexistente", new EditarOfertaRequest(List.of("ARG-10"))));
  }

  @Test
  void cancelarOferta_marcaComoCancelada() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    Propuesta propuesta = Propuesta.builder()
        .id("o-1")
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of())
        .build();

    subasta.agregarOferta(propuesta);
    repositorioSubastas.guardar(subasta);

    service.cancelarOferta(lucas.getId(), "s-1", "o-1");

    subasta = repositorioSubastas.buscarPorId("s-1", new CamposSubasta(false, true));

    assertEquals(EstadoProceso.CANCELADO, buscarOfertaEn(subasta, "o-1").getEstadoActual().getValor());
  }

  @Test
  void cancelarOferta_ofertaInexistente_lanzaExcepcion() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    assertThrows(BadRequestException.class,
        () -> service.cancelarOferta(lucas.getId(), "s-1", "inexistente"));
  }

  @Test
  void obtenerSubastas_misActivas_retornaMiSubastaActivaDto() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, sofia.getId(), null, "ACTIVA");

    PaginaResultado<?> resultado = service.obtenerSubastas(sofia.getId(), filtros);

    assertEquals(1, resultado.contenido().size());
    assertInstanceOf(MiSubastaActivaDto.class, resultado.contenido().get(0));
    assertEquals("s-1", ((MiSubastaActivaDto) resultado.contenido().get(0)).getId());
  }

  @Test
  void obtenerSubastas_misFinalizadas_retornaMiSubastaFinalizadaDto() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(3))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, sofia.getId(), null, "FINALIZADA");

    PaginaResultado<?> resultado = service.obtenerSubastas(sofia.getId(), filtros);

    assertEquals(1, resultado.contenido().size());
    assertInstanceOf(MiSubastaFinalizadaDto.class, resultado.contenido().get(0));
  }

  @Test
  void obtenerSubastas_misFinalizadas_conOfertaAceptada_tieneOfertaGanadora() {
    Propuesta propuesta = Propuesta.builder()
        .id("o-1")
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of())
        .build();
    propuesta.aceptar(sofia.getId());

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(3))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi)
        .ofertas(List.of(propuesta))
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, sofia.getId(), null, "FINALIZADA");

    PaginaResultado<?> resultado = service.obtenerSubastas(sofia.getId(), filtros);

    MiSubastaFinalizadaDto dto = (MiSubastaFinalizadaDto) resultado.contenido().get(0);
    assertNotNull(dto.getOfertaGanadora());
    assertEquals("o-1", dto.getOfertaGanadora().getId());
  }

  @Test
  void obtenerSubastas_misFinalizadas_sinOfertaAceptada_ofertaGanadoraEsNull() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(3))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, sofia.getId(), null, "FINALIZADA");

    PaginaResultado<?> resultado = service.obtenerSubastas(sofia.getId(), filtros);

    MiSubastaFinalizadaDto dto = (MiSubastaFinalizadaDto) resultado.contenido().get(0);
    assertNull(dto.getOfertaGanadora());
  }

  @Test
  void obtenerSubastas_misActivas_noRetornaFinalizadas() {
    Subasta activa = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    Subasta finalizada = Subasta.builder()
        .id("s-2")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(3))
        .fechaCierre(LocalDateTime.now().minusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(activa);
    repositorioSubastas.guardar(finalizada);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, sofia.getId(), null, "ACTIVA");

    PaginaResultado<?> resultado = service.obtenerSubastas(sofia.getId(), filtros);

    assertEquals(1, resultado.contenido().size());
    assertEquals("s-1", ((MiSubastaActivaDto) resultado.contenido().get(0)).getId());
  }

  @Test
  void obtenerSubastas_conParticipante_retornaSubastaParticipoDto() {
    Propuesta propuesta = Propuesta.builder()
        .id("o-1")
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of())
        .build();

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .ofertas(List.of(propuesta))
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, null, lucas.getId(), "ACTIVA");

    PaginaResultado<?> resultado = service.obtenerSubastas(lucas.getId(), filtros);

    mongoTemplate.findAll(Subasta.class).forEach(s -> {
      System.out.println("Subasta: " + s.getId());
      s.getOfertas().forEach(o -> {
        System.out.println("  Oferta: " + o.getId());
        System.out.println("  Autor: " + o.getAutor());
        System.out.println("  Autor id: " + o.getAutor().getId());
        System.out.println("  EstadoActual: " + o.getEstadoActual());
      });
    });
    assertEquals(1, resultado.contenido().size());
    assertInstanceOf(SubastaParticipoDto.class, resultado.contenido().get(0));
    SubastaParticipoDto dto = (SubastaParticipoDto) resultado.contenido().get(0);
    assertEquals("s-1", dto.getId());
    assertEquals("o-1", dto.getTuOferta().getId());
  }

  @Test
  void obtenerSubastas_conParticipante_ofertaCancelada_noRetornaSubasta() {
    Propuesta propuesta = Propuesta.builder()
        .id("o-1")
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of())
        .build();
    propuesta.cancelar(lucas.getId());

    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .ofertas(List.of(propuesta))
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, null, lucas.getId(), "ACTIVA");

    PaginaResultado<?> resultado = service.obtenerSubastas(lucas.getId(), filtros);

    assertTrue(resultado.contenido().isEmpty());
  }

  @Test
  void obtenerSubastas_conParticipante_sinOferta_noRetornaSubasta() {
    Subasta subasta = Subasta.builder()
        .id("s-1")
        .autor(sofia)
        .fechaInicio(LocalDateTime.now().minusHours(1))
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .build();

    repositorioSubastas.guardar(subasta);

    SubastasFiltro filtros = new SubastasFiltro(1, 10, null, lucas.getId(), "ACTIVA");

    PaginaResultado<?> resultado = service.obtenerSubastas(lucas.getId(), filtros);

    assertTrue(resultado.contenido().isEmpty());
  }

  private Propuesta buscarOfertaEn(Subasta subasta, String ofertaId) {
    return subasta.getOfertas().stream()
        .filter(o -> o.getId().equals(ofertaId))
        .findFirst()
        .orElseThrow();
  }
}