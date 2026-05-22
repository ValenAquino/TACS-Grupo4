package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.*;

import app.MongoTestBase;
import app.dto.PropuestaDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CrearPropuestaRequest;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.model.entities.*;
import app.servicios.ServicioPropuesta;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ServicioPropuestaTest extends MongoTestBase {

  @Autowired
  ServicioPropuesta propuestaService;

  private Perfil lucas;
  private Perfil sofia;

  private Figurita messi;
  private Figurita mbappe;

  @BeforeEach
  void setUp() {

    // Figuritas
    messi = new Figurita(
        "ARG-10",
        10,
        "Messi",
        Seleccion.ARGENTINA,
        null
    );

    mbappe = new Figurita(
        "FRA-10",
        10,
        "Mbappe",
        Seleccion.FRANCIA,
        null
    );

    // Usuarios
    Usuario user1 = new Usuario(
        "u-1000",
        Rol.USUARIO,
        "lucas",
        "fiscella"
    );

    Usuario user2 = new Usuario(
        "u-1001",
        Rol.USUARIO,
        "sofia",
        "fiscella"
    );

    // Colección Lucas
    Coleccion colec1 = new Coleccion("c-1000");

    colec1.agregarFaltante(messi);

    colec1.agregarRepetida(
        new FiguritaIntercambiable(
            mbappe,
            2,
            List.of(MetodoIntercambio.INTERCAMBIO)
        )
    );

    // Colección Sofía
    Coleccion colec2 = new Coleccion("c-1001");

    colec2.agregarFaltante(mbappe);

    colec2.agregarRepetida(
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.INTERCAMBIO)
        )
    );

    // Perfiles
    lucas = Perfil.builder()
        .id("1000")
        .usuario(user1)
        .nombre("Lucas")
        .coleccion(colec1)
        .build();

    sofia = Perfil.builder()
        .id("1001")
        .usuario(user2)
        .nombre("Sofía")
        .coleccion(colec2)
        .build();

    // Persistencia
    repositorioUsuarios.guardar(user1);
    repositorioUsuarios.guardar(user2);

    repositorioFiguritas.guardar(messi);
    repositorioFiguritas.guardar(mbappe);

    repositorioColecciones.guardar(colec1);
    repositorioColecciones.guardar(colec2);

    repositorioPerfiles.guardar(lucas);
    repositorioPerfiles.guardar(sofia);
  }

  @Test
  void crearPropuestaDevuelveDto() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    PropuestaDto resultado =
        propuestaService.crearPropuesta("1000", request);

    assertEquals("1000", resultado.getAutor().getId());
    assertEquals("1001", resultado.getDestinatario().getId());
    assertEquals("ARG-10", resultado.getFiguritaBuscada().getId());

    assertEquals(
        EstadoProceso.PENDIENTE,
        resultado.getEstado()
    );
  }

  @Test
  void crearPropuestaGuardaFiguritasOfrecidasCorrectamente() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    PropuestaDto resultado =
        propuestaService.crearPropuesta("1000", request);

    assertEquals(
        1,
        resultado.getFiguritasOfrecidas().size()
    );

    assertEquals(
        "FRA-10",
        resultado.getFiguritasOfrecidas().get(0).getId()
    );
  }

  @Test
  void crearPropuestaPersisteLaEntidad() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    PropuestaDto resultado =
        propuestaService.crearPropuesta("1000", request);

    Propuesta propuestaGuardada =
        repositorioPropuestas.buscarPorId(
            resultado.getId()
        );

    assertNotNull(propuestaGuardada);

    assertEquals(
        "1000",
        propuestaGuardada.getAutor().getId()
    );
  }

  @Test
  void crearPropuestaUsuarioOrigenNoExisteLanzaNotFoundException() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    assertThrows(
        NotFoundException.class,
        () -> propuestaService.crearPropuesta(
            "9999",
            request
        )
    );
  }

  @Test
  void crearPropuestaUsuarioDestinoNoExisteLanzaNotFoundException() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "9999",
            "ARG-10",
            List.of("FRA-10")
        );

    assertThrows(
        NotFoundException.class,
        () -> propuestaService.crearPropuesta(
            "1000",
            request
        )
    );
  }

  @Test
  void crearPropuestaReservaFiguritasOfrecidas() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    propuestaService.crearPropuesta(
        "1000",
        request
    );

    Perfil autor =
        repositorioPerfiles.buscarPorId("1000");

    FiguritaIntercambiable repetida =
        autor.getColeccion()
            .getRepetidas()
            .stream()
            .filter(r ->
                r.getFigurita()
                    .getId()
                    .equals("FRA-10")
            )
            .findFirst()
            .orElseThrow();

    assertEquals(
        1,
        repetida.getCantidadReservada()
    );
  }

  @Test
  void crearPropuestaFiguritaBuscadaInexistenteLanzaException() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "NO_EXISTE",
            List.of("FRA-10")
        );

    assertThrows(
        NotFoundException.class,
        () -> propuestaService.crearPropuesta(
            "1000",
            request
        )
    );
  }

  @Test
  void crearPropuestaFiguritaOfrecidaInexistenteLanzaException() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("NO_EXISTE")
        );

    assertThrows(
        NotFoundException.class,
        () -> propuestaService.crearPropuesta(
            "1000",
            request
        )
    );
  }

  @Test
  void crearPropuestaSinDisponibilidadLanzaException() {

    Perfil autor =
        repositorioPerfiles.buscarPorId("1000");

    FiguritaIntercambiable repetida =
        autor.getColeccion()
            .getRepetidas()
            .get(0);

    repetida.reservar(
        MetodoIntercambio.INTERCAMBIO
    );

    repetida.reservar(
        MetodoIntercambio.INTERCAMBIO
    );

    repositorioColecciones.guardar(
        autor.getColeccion()
    );

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    assertThrows(
        BadRequestException.class,
        () -> propuestaService.crearPropuesta(
            "1000",
            request
        )
    );
  }

  @Test
  void crearPropuestaGuardaEstadoPendiente() {

    CrearPropuestaRequest request =
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        );

    PropuestaDto dto =
        propuestaService.crearPropuesta(
            "1000",
            request
        );

    Propuesta propuesta =
        repositorioPropuestas
            .buscarPorId(dto.getId());

    assertEquals(
        EstadoProceso.PENDIENTE,
        propuesta
            .obtenerEstadoActual()
            .getValor()
    );
  }

  @Test
  void aceptarActualizaEstado() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.aceptar(
        propuesta.getId(),
        "1001"
    );

    Propuesta actualizada =
        repositorioPropuestas.buscarPorId(
            propuesta.getId()
        );

    assertEquals(
        EstadoProceso.ACEPTADO,
        actualizada.obtenerEstadoActual().getValor()
    );
  }

  @Test
  void aceptarReservaLaFiguritaBuscada() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.aceptar(
        propuesta.getId(),
        "1001"
    );

    Perfil destinatario =
        repositorioPerfiles.buscarPorId("1001");

    FiguritaIntercambiable repetida =
        destinatario.getColeccion()
            .getRepetidas()
            .stream()
            .filter(r ->
                r.getFigurita().getId()
                    .equals("ARG-10")
            )
            .findFirst()
            .orElseThrow();

    // se reserva y luego cambioConcretado la elimina
    assertEquals(
        1,
        repetida.getCantidadExistente()
    );

    assertEquals(
        0,
        repetida.getCantidadReservada()
    );
  }

  @Test
  void aceptarEliminaFaltanteDelAutor() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.aceptar(
        propuesta.getId(),
        "1001"
    );

    Perfil autor =
        repositorioPerfiles.buscarPorId("1000");

    assertFalse(
        autor.getColeccion()
            .tieneFaltante(messi)
    );
  }

  @Test
  void aceptarEliminaFaltanteDelDestinatario() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.aceptar(
        propuesta.getId(),
        "1001"
    );

    Perfil destinatario =
        repositorioPerfiles.buscarPorId("1001");

    assertFalse(
        destinatario.getColeccion()
            .tieneFaltante(mbappe)
    );
  }

  @Test
  void aceptarReduceCantidadRepetidaDelAutor() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.aceptar(
        propuesta.getId(),
        "1001"
    );

    Perfil autor =
        repositorioPerfiles.buscarPorId("1000");

    FiguritaIntercambiable repetida =
        autor.getColeccion()
            .getRepetidas()
            .stream()
            .findFirst()
            .orElseThrow();

    assertEquals(
        1,
        repetida.getCantidadExistente()
    );
  }

  @Test
  void aceptarConPerfilIncorrectoLanzaExcepcion() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    assertThrows(
        BadRequestException.class,
        () -> propuestaService.aceptar(
            propuesta.getId(),
            "1000"
        )
    );
  }

  @Test
  void aceptarPropuestaInexistenteLanzaExcepcion() {

    assertThrows(
        NotFoundException.class,
        () -> propuestaService.aceptar(
            "id-inexistente",
            "1001"
        )
    );
  }

  @Test
  void rechazarActualizaEstado() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.rechazar(
        propuesta.getId(),
        "1001"
    );

    Propuesta actualizada =
        repositorioPropuestas.buscarPorId(
            propuesta.getId()
        );

    assertEquals(
        EstadoProceso.RECHAZADO,
        actualizada.obtenerEstadoActual().getValor()
    );
  }

  @Test
  void cancelarActualizaEstado() {

    Propuesta propuesta = Propuesta.builder()
        .autor(lucas)
        .destinatario(sofia)
        .figuritaBuscada(messi)
        .figuritasOfrecidas(List.of(mbappe))
        .build();

    repositorioPropuestas.guardar(propuesta);

    propuestaService.cancelar(
        propuesta.getId(),
        "1000"
    );

    Propuesta actualizada =
        repositorioPropuestas.buscarPorId(
            propuesta.getId()
        );

    assertEquals(
        EstadoProceso.CANCELADO,
        actualizada.obtenerEstadoActual().getValor()
    );
  }

  @Test
  void buscarPropuestasRecibidasRetornaResultados() {

    propuestaService.crearPropuesta(
        "1000",
        new CrearPropuestaRequest(
            "1001",
            "ARG-10",
            List.of("FRA-10")
        )
    );

    PropuestasFiltro filtro =
        new PropuestasFiltro(
            "RECIBIDAS",
            0,
            10,
            null
        );

    PaginaResultado<PropuestaDto> resultado =
        propuestaService.buscarPropuestas(
            "1001",
            filtro
        );

    assertEquals(1,
        resultado.cantidadDeElementos());

    assertEquals(
        "1000",
        resultado.contenido()
            .get(0)
            .getAutor()
            .getId()
    );
  }

  @Test
  void buscarPropuestasConTipoInvalidoDevuelvePaginaVacia() {

    PropuestasFiltro filtro =
        new PropuestasFiltro(
            "INVALIDO",
            0,
            10,
            null
        );

    PaginaResultado<PropuestaDto> resultado =
        propuestaService.buscarPropuestas(
            "1000",
            filtro
        );

    assertTrue(
        resultado.contenido().isEmpty()
    );

    assertEquals(
        0,
        resultado.cantidadDeElementos()
    );
  }

}