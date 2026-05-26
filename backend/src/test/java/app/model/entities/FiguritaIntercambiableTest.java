package app.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import app.exceptions.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiguritaIntercambiableTest {

  private FiguritaIntercambiable fi;

  @BeforeEach
  void setUp() {
    Figurita messi = new Figurita(
        "ARG-10",
        10,
        "Messi",
        Seleccion.ARGENTINA
    );

    fi = new FiguritaIntercambiable(
        messi,
        3,
        List.of(MetodoIntercambio.INTERCAMBIO)
    );
  }

  @Test
  void soporta_cuandoMetodoExiste_retornaTrue() {
    assertTrue(fi.soporta(MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void soporta_cuandoMetodoNoExiste_retornaFalse() {
    assertFalse(fi.soporta(MetodoIntercambio.SUBASTA));
  }

  @Test
  void getCantidadDisponible_sinReservas_retornaCantidadExistente() {
    assertEquals(3, fi.getCantidadDisponible());
  }

  @Test
  void getCantidadDisponible_conReservas_retornaCantidadRestante() {
    fi.reservar(MetodoIntercambio.INTERCAMBIO);

    assertEquals(2, fi.getCantidadDisponible());
  }

  @Test
  void reservar_incrementaCantidadReservada() {
    fi.reservar(MetodoIntercambio.INTERCAMBIO);

    assertEquals(1, fi.getCantidadReservada());
  }

  @Test
  void reservar_conMetodoNoSoportado_lanzaExcepcion() {
    BadRequestException ex = assertThrows(
        BadRequestException.class,
        () -> fi.reservar(MetodoIntercambio.SUBASTA)
    );

    assertEquals(
        "Esta figurita no soporta el metodo seleccionado",
        ex.getMessage()
    );
  }

  @Test
  void reservar_sinDisponibilidad_lanzaExcepcion() {
    fi.reservar(MetodoIntercambio.INTERCAMBIO);
    fi.reservar(MetodoIntercambio.INTERCAMBIO);
    fi.reservar(MetodoIntercambio.INTERCAMBIO);

    BadRequestException ex = assertThrows(
        BadRequestException.class,
        () -> fi.reservar(MetodoIntercambio.INTERCAMBIO)
    );

    assertEquals(
        "No hay figuritas disponibles para reservar",
        ex.getMessage()
    );
  }

  @Test
  void eliminarReserva_decrementaReservadas() {
    fi.reservar(MetodoIntercambio.INTERCAMBIO);

    fi.eliminarReserva();

    assertEquals(0, fi.getCantidadReservada());
  }

  @Test
  void eliminarReserva_sinReservas_lanzaExcepcion() {
    RuntimeException ex = assertThrows(
        RuntimeException.class,
        () -> fi.eliminarReserva()
    );

    assertEquals(
        "No hay reservas para eliminar",
        ex.getMessage()
    );
  }

  @Test
  void cambioConcretado_decrementaCantidadExistente() {
    fi.cambioConcretado();

    assertEquals(2, fi.getCantidadExistente());
  }

  @Test
  void cambioConcretado_conReserva_eliminaReserva() {
    fi.reservar(MetodoIntercambio.INTERCAMBIO);

    fi.cambioConcretado();

    assertEquals(2, fi.getCantidadExistente());
    assertEquals(0, fi.getCantidadReservada());
  }

  @Test
  void cambioConcretado_sinReserva_noModificaReservadas() {
    fi.cambioConcretado();

    assertEquals(0, fi.getCantidadReservada());
    assertEquals(2, fi.getCantidadExistente());
  }

  @Test
  void cambioConcretado_sinStock_lanzaExcepcion() {
    fi.cambioConcretado();
    fi.cambioConcretado();
    fi.cambioConcretado();

    BadRequestException ex = assertThrows(
        BadRequestException.class,
        () -> fi.cambioConcretado()
    );

    assertEquals(
        "No hay cantidad existente de esta figurita",
        ex.getMessage()
    );
  }
}