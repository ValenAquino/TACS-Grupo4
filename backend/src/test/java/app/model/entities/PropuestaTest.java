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

    messi =
        new Figurita(
            "ARG10",
            10,
            "Messi",
            Seleccion.ARGENTINA,
            null
        );

    diMaria =
        new Figurita(
            "ARG11",
            11,
            "Di María",
            Seleccion.ARGENTINA,
            null
        );

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
        )
    );
  }

  @Test
  void deberiaAceptarPropuestaPendiente() {
    propuesta.aceptar(destino.getId());

    assertEquals(
        EstadoProceso.ACEPTADO,
        propuesta.obtenerEstadoActual().getValor()
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
        propuesta.obtenerEstadoActual().getValor()
    );
  }

  @Test
  void deberiaSeleccionarPropuestaPendiente() {
    propuesta.seleccionar(destino.getId());

    assertEquals(
        EstadoProceso.SELECCIONADO,
        propuesta.obtenerEstadoActual().getValor()
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
        propuesta.obtenerEstadoActual().getValor()
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
  void obtenerEstadoActual_listaVacia_inicializaPendiente() {

    propuesta.setEstado(new ArrayList<>());

    EstadoPropuesta estado =
        propuesta.obtenerEstadoActual();

    assertEquals(
        EstadoProceso.PENDIENTE,
        estado.getValor()
    );

    assertEquals(
        1,
        propuesta.getEstado().size()
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
  void rechazarLiberaReservasDeFiguritasOfrecidas() {

    FiguritaIntercambiable repetida =
        origen.getColeccion()
            .getRepetidas()
            .get(0);

    repetida.reservar(MetodoIntercambio.INTERCAMBIO);

    assertEquals(
        1,
        repetida.getCantidadReservada()
    );

    propuesta.rechazar(destino.getId());

    assertEquals(
        0,
        repetida.getCantidadReservada()
    );
  }

  @Test
  void cancelarLiberaReservasDeFiguritasOfrecidas() {

    FiguritaIntercambiable repetida =
        origen.getColeccion()
            .getRepetidas()
            .get(0);

    repetida.reservar(MetodoIntercambio.INTERCAMBIO);

    assertEquals(
        1,
        repetida.getCantidadReservada()
    );

    propuesta.cancelar(origen.getId());

    assertEquals(
        0,
        repetida.getCantidadReservada()
    );
  }
}