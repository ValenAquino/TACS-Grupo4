package app.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FiguritaIntercambiableTest {

  private FiguritaIntercambiable fi;

  @BeforeEach
  void setUp() {
    Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
    fi = new FiguritaIntercambiable(messi, 3, List.of(MetodoIntercambio.INTERCAMBIO));
  }

  @Test
  void hayCantidadDisponible_cuandoHayStock_retornaTrue() {
    assertTrue(fi.hayCantidadDisponible());
  }

  @Test
  void hayCantidadDisponible_cuandoTodoReservado_retornaFalse() {
    fi.reservarFiguritaIntercambiable();
    fi.reservarFiguritaIntercambiable();
    fi.reservarFiguritaIntercambiable();
    assertFalse(fi.hayCantidadDisponible());
  }

  @Test
  void reservar_decrementaDisponibilidad() {
    fi.reservarFiguritaIntercambiable();
    assertEquals(1, fi.getCantidadReservada());
  }

  @Test
  void reservar_sinStock_lanzaExcepcion() {
    fi.reservarFiguritaIntercambiable();
    fi.reservarFiguritaIntercambiable();
    fi.reservarFiguritaIntercambiable();
    assertThrows(RuntimeException.class, () -> fi.reservarFiguritaIntercambiable());
  }

  @Test
  void eliminarReserva_decrementaReservadas() {
    fi.reservarFiguritaIntercambiable();
    fi.eliminarReserva();
    assertEquals(0, fi.getCantidadReservada());
  }

  @Test
  void eliminarReserva_sinReservas_lanzaExcepcion() {
    assertThrows(RuntimeException.class, () -> fi.eliminarReserva());
  }

  @Test
  void cambioConcretado_decrementaStock() {
    fi.cambioConcretado();
    assertEquals(2, fi.getCantidadExistente());
  }

  @Test
  void cambioConcretado_sinStock_lanzaExcepcion() {
    fi.cambioConcretado();
    fi.cambioConcretado();
    fi.cambioConcretado();
    assertThrows(RuntimeException.class, () -> fi.cambioConcretado());
  }

  @Test
  void cambioConcretado_conReserva_eliminaReserva() {
    fi.reservarFiguritaIntercambiable();
    fi.cambioConcretado();
    assertEquals(0, fi.getCantidadReservada());
    assertEquals(2, fi.getCantidadExistente());
  }
}
