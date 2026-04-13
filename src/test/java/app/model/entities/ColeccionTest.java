package app.model.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColeccionTest {
  Figurita messi = new Figurita("ARG-10", 10, "Messi",     Seleccion.ARGENTINA);
  Figurita diMaria = new Figurita("ARG-11", 11, "Di María",  Seleccion.ARGENTINA);
  Figurita lautaro = new Figurita("ARG-9",   9, "Lautaro",   Seleccion.ARGENTINA);


  @Test
  void agregarNuevaFaltante() {
    Coleccion coleccion = new Coleccion("10");
    coleccion.agregarFaltante(messi);

    assertTrue(coleccion.getFaltantes().contains(messi));
    assertEquals(1, coleccion.getFaltantes().size());
  }

  @Test
  void agregarNuevaFaltante_duplicada_tiraError() {
    Coleccion coleccion = new Coleccion("10");
    coleccion.agregarFaltante(messi);

    assertThrows(RuntimeException.class, () -> coleccion.agregarFaltante(messi));
  }
}
