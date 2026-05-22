package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.*;

import app.MongoTestBase;
import app.dto.PropuestaDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CrearPropuestaRequest;
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
    Usuario user = new Usuario("u-1000", Rol.USUARIO, "lucas", "fiscella");
    repositorioUsuarios.guardar(user);

    Coleccion colec = new Coleccion("c-1000");

    lucas = Perfil.builder()
        .id("1000")
        .usuario(user)
        .nombre("Lucas")
        .coleccion(colec)
        .build();

    repositorioColecciones.guardar(colec);
    repositorioPerfiles.guardar(lucas);

    user = new Usuario("u-1001", Rol.USUARIO, "sofia", "fiscella");
    repositorioUsuarios.guardar(user);

    colec = new Coleccion("c-1001");

    sofia = Perfil.builder()
        .id("1001")
        .usuario(user)
        .nombre("Sofía")
        .coleccion(colec)
        .build();

    repositorioColecciones.guardar(colec);
    repositorioPerfiles.guardar(sofia);

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

    repositorioFiguritas.guardar(messi);
    repositorioFiguritas.guardar(mbappe);
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