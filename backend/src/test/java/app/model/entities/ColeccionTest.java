package app.model.entities;

import app.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColeccionTest {

  Figurita messi = new Figurita(
      "ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);

  Figurita diMaria = new Figurita(
      "ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);

  @Test
  void agregarNuevaFaltante() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.agregarFaltante(messi);
    coleccion.agregarFaltante(diMaria);

    assertTrue(coleccion.getFaltantes().contains(messi));
    assertEquals(2, coleccion.getFaltantes().size());
  }

  @Test
  void agregarNuevaFaltante_duplicada_tiraError() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.agregarFaltante(messi);

    assertThrows(
        BadRequestException.class,
        () -> coleccion.agregarFaltante(messi)
    );
  }

  @Test
  void agregarNuevaRepetida() {
    Coleccion coleccion = new Coleccion("10");

    FiguritaIntercambiable repetida =
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        );

    FiguritaIntercambiable repetida2 =
        new FiguritaIntercambiable(
            diMaria,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        );

    coleccion.agregarRepetida(repetida);
    coleccion.agregarRepetida(repetida2);

    assertEquals(2, coleccion.getRepetidas().size());
  }

  @Test
  void agregarNuevaRepetidaDuplicada_acumulaCantidad() {
    Coleccion coleccion = new Coleccion("10");

    FiguritaIntercambiable repetida =
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        );

    FiguritaIntercambiable repetida2 =
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        );

    coleccion.agregarRepetida(repetida);
    coleccion.agregarRepetida(repetida2);

    assertEquals(1, coleccion.getRepetidas().size());
    assertEquals(4, repetida.getCantidadExistente());
  }

  @Test
  void eliminarFaltante_existente_laElimina() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.agregarFaltante(messi);

    coleccion.eliminarFaltante(messi);

    assertFalse(
        coleccion.getFaltantes().contains(messi)
    );
  }

  @Test
  void eliminarFaltante_inexistente_lanzaExcepcion() {
    Coleccion coleccion = new Coleccion("10");

    assertThrows(
        BadRequestException.class,
        () -> coleccion.eliminarFaltante(messi)
    );
  }

  @Test
  void eliminarRepetida_existente_laElimina() {
    Coleccion coleccion = new Coleccion("10");

    FiguritaIntercambiable repetida =
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        );

    coleccion.agregarRepetida(repetida);

    coleccion.eliminarRepetida(messi);

    assertTrue(
        coleccion.getRepetidas().isEmpty()
    );
  }

  @Test
  void tieneFaltante_existente_retornaTrue() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.agregarFaltante(messi);

    assertTrue(
        coleccion.tieneFaltante(messi)
    );
  }

  @Test
  void tieneFaltante_inexistente_retornaFalse() {
    Coleccion coleccion = new Coleccion("10");

    assertFalse(
        coleccion.tieneFaltante(messi)
    );
  }

  @Test
  void tieneRepetida_existente_retornaTrue() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.agregarRepetida(
        new FiguritaIntercambiable(
            messi,
            2,
            List.of(MetodoIntercambio.SUBASTA)
        )
    );

    assertTrue(
        coleccion.tieneRepetida(messi)
    );
  }

  @Test
  void tieneRepetida_inexistente_retornaFalse() {
    Coleccion coleccion = new Coleccion("10");

    assertFalse(
        coleccion.tieneRepetida(messi)
    );
  }

  @Test
  void descontarRepetida_reduceCantidad() {
    Coleccion coleccion = new Coleccion("10");

    FiguritaIntercambiable repetida =
        new FiguritaIntercambiable(
            messi,
            3,
            List.of(MetodoIntercambio.SUBASTA)
        );

    coleccion.agregarRepetida(repetida);

    coleccion.descontarRepetida(messi);

    assertEquals(
        2,
        repetida.getCantidadExistente()
    );
  }

  @Test
  void descontarRepetida_ultimaUnidad_eliminaEntrada() {
    Coleccion coleccion = new Coleccion("10");

    coleccion.agregarRepetida(
        new FiguritaIntercambiable(
            messi,
            1,
            List.of(MetodoIntercambio.SUBASTA)
        )
    );

    coleccion.descontarRepetida(messi);

    assertTrue(
        coleccion.getRepetidas().isEmpty()
    );
  }

  @Test
  void descontarRepetida_inexistente_lanzaExcepcion() {
    Coleccion coleccion = new Coleccion("10");

    assertThrows(
        BadRequestException.class,
        () -> coleccion.descontarRepetida(messi)
    );
  }

  @Test
  void reservarRepetidas_incrementaReservadas() {
    Coleccion coleccion = new Coleccion("10");

    FiguritaIntercambiable repetida =
        new FiguritaIntercambiable(
            messi,
            3,
            List.of(MetodoIntercambio.INTERCAMBIO)
        );

    coleccion.agregarRepetida(repetida);

    coleccion.reservarRepetidas(
        List.of(messi),
        MetodoIntercambio.INTERCAMBIO
    );

    assertEquals(
        1,
        repetida.getCantidadReservada()
    );
  }
}