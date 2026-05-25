package app.model.entities;

import app.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropuestaTest {

  private Perfil origen;
  private Perfil destino;

  private Figurita messi;
  private Figurita diMaria;

  private Propuesta propuesta;

  @BeforeEach
  void setUp() {

    Usuario user1 =
        new Usuario("u-1", Rol.USUARIO,"lucas","fiscella");

    Usuario user2 =
        new Usuario("u-2", Rol.USUARIO,"juan","perez");

    origen = Perfil.builder()
        .id("1")
        .usuario(user1)
        .nombre("Origen")
        .coleccion(new Coleccion("c1"))
        .mediosDeContacto(
            List.of(
                new MedioDeContacto(
                    MedioComunicacion.TELEGRAM,
                    "@origen"
                )
            )
        )
        .build();

    destino = Perfil.builder()
        .id("2")
        .usuario(user2)
        .nombre("Destino")
        .coleccion(new Coleccion("c2"))
        .mediosDeContacto(
            List.of(
                new MedioDeContacto(
                    MedioComunicacion.TELEGRAM,
                    "@destino"
                )
            )
        )
        .build();

    messi = Figurita.builder()
        .id("ARG10")
        .numero(10)
        .jugador("Messi")
        .seleccion(Seleccion.ARGENTINA)
        .build();

    diMaria = Figurita.builder()
        .id("ARG11")
        .numero(11)
        .jugador("Di María")
        .seleccion(Seleccion.ARGENTINA)
        .build();

    origen.getColeccion()
        .agregarFaltante(messi);

    origen.getColeccion()
        .agregarRepetida(
            new FiguritaIntercambiable(
                diMaria,
                2,
                List.of(MetodoIntercambio.INTERCAMBIO)
            )
        );

    destino.getColeccion()
        .agregarFaltante(diMaria);

    destino.getColeccion()
        .agregarRepetida(
            new FiguritaIntercambiable(
                messi,
                2,
                List.of(MetodoIntercambio.INTERCAMBIO)
            )
        );

    propuesta = new Propuesta(
        "123",
        origen,
        destino,
        List.of(diMaria),
        messi,
        new ArrayList<>(
            List.of(
                new EstadoPropuesta(
                    LocalDateTime.now(),
                    EstadoProceso.PENDIENTE
                )
            )
        ),
        new EstadoPropuesta(
            LocalDateTime.now(),
            EstadoProceso.PENDIENTE
        )
    );
  }

  @Test
  void deberiaAceptarPropuestaPendiente() {

    propuesta.aceptar(destino.getId());

    assertEquals(
        EstadoProceso.ACEPTADO,
        propuesta.getEstadoActual().getValor()
    );

    assertEquals(
        EstadoProceso.ACEPTADO,
        propuesta.getEstado()
            .get(propuesta.getEstado().size() - 1)
            .getValor()
    );
  }

  @Test
  void deberiaRechazarPropuestaPendiente() {

    origen.getColeccion()
        .getRepetidas()
        .get(0)
        .reservar(MetodoIntercambio.INTERCAMBIO);

    propuesta.rechazar(destino.getId());

    assertEquals(
        EstadoProceso.RECHAZADO,
        propuesta.getEstadoActual().getValor()
    );
  }

  @Test
  void deberiaSeleccionarPropuestaPendiente() {
    propuesta.seleccionar(destino.getId());

    assertEquals(
        EstadoProceso.SELECCIONADO,
        propuesta.getEstadoActual().getValor()
    );
  }

  @Test
  void deberiaCancelarPropuestaPendiente() {

    origen.getColeccion()
        .getRepetidas()
        .get(0)
        .reservar(MetodoIntercambio.INTERCAMBIO);

    propuesta.cancelar(origen.getId());

    assertEquals(
        EstadoProceso.CANCELADO,
        propuesta.getEstadoActual().getValor()
    );
  }

  @Test
  void cancelarConUsuarioNoAutor_lanzaExcepcion() {
    assertThrows(
        BadRequestException.class,
        () -> propuesta.cancelar(destino.getId())
    );
  }

  @Test
  void aceptarConUsuarioNoDestino_lanzaExcepcion() {

    Perfil otro =
        Perfil.builder()
            .id("3")
            .build();

    assertThrows(
        BadRequestException.class,
        () -> propuesta.aceptar(otro.getId())
    );
  }

  @Test
  void rechazarConUsuarioNoDestino_lanzaExcepcion() {

    Perfil otro =
        Perfil.builder()
            .id("3")
            .build();

    assertThrows(
        BadRequestException.class,
        () -> propuesta.rechazar(otro.getId())
    );
  }

  @Test
  void noDebeAceptarPropuestaYaRespondida() {

    origen.getColeccion()
        .getRepetidas()
        .get(0)
        .reservar(MetodoIntercambio.INTERCAMBIO);


    propuesta.rechazar(destino.getId());

    assertThrows(
        BadRequestException.class,
        () -> propuesta.aceptar(destino.getId())
    );
  }

  @Test
  void propuestaNueva_inicializaPendientePorDefecto() {

    Propuesta propuesta = Propuesta.builder()
        .autor(origen)
        .destinatario(destino)
        .build();

    assertEquals(
        EstadoProceso.PENDIENTE,
        propuesta.getEstadoActual().getValor()
    );

    assertEquals(
        1,
        propuesta.getEstado().size()
    );

    assertEquals(
        EstadoProceso.PENDIENTE,
        propuesta.getEstado()
            .get(0)
            .getValor()
    );
  }

  @Test
  void aceptarEjecutaIntercambioCorrectamente() {

    propuesta.aceptar(destino.getId());

    assertFalse(
        origen.getColeccion()
            .tieneFaltante(messi)
    );

    assertFalse(
        destino.getColeccion()
            .tieneFaltante(diMaria)
    );
  }


  @Test
  void noDebeCancelarPropuestaYaRespondida() {

    origen.getColeccion()
        .getRepetidas()
        .get(0)
        .reservar(MetodoIntercambio.INTERCAMBIO);


    propuesta.rechazar(destino.getId());

    assertThrows(
        BadRequestException.class,
        () -> propuesta.cancelar(origen.getId())
    );
  }

  @Test
  void noDebeRechazarPropuestaYaRespondida() {

    propuesta.aceptar(destino.getId());

    assertThrows(
        BadRequestException.class,
        () -> propuesta.rechazar(destino.getId())
    );
  }

  @Test
  void rechazarLiberaReservasYActualizaEstado() {

    FiguritaIntercambiable repetida =
        origen.getColeccion()
            .getRepetidas()
            .get(0);

    repetida.reservar(MetodoIntercambio.INTERCAMBIO);

    propuesta.rechazar(destino.getId());

    assertEquals(
        0,
        repetida.getCantidadReservada()
    );

    assertEquals(
        EstadoProceso.RECHAZADO,
        propuesta.getEstadoActual().getValor()
    );

    assertEquals(
        EstadoProceso.RECHAZADO,
        propuesta.getEstado()
            .get(propuesta.getEstado().size() - 1)
            .getValor()
    );
  }

  @Test
  void cancelarLiberaReservasYActualizaEstado() {

    FiguritaIntercambiable repetida =
        origen.getColeccion()
            .getRepetidas()
            .get(0);

    repetida.reservar(MetodoIntercambio.INTERCAMBIO);

    propuesta.cancelar(origen.getId());

    assertEquals(
        0,
        repetida.getCantidadReservada()
    );

    assertEquals(
        EstadoProceso.CANCELADO,
        propuesta.getEstadoActual().getValor()
    );

    assertEquals(
        EstadoProceso.CANCELADO,
        propuesta.getEstado()
            .get(propuesta.getEstado().size() - 1)
            .getValor()
    );
  }
}