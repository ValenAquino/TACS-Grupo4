package app.model.entities.filtros;

import app.model.entities.Seleccion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FiguritasFiltroTest {

  @Test
  void deberiaCrearRecordCorrectamente() {
    FiguritasFiltro filtro = new FiguritasFiltro(
        "abc123",
        10,
        Seleccion.ARGENTINA,
        "Messi"
    );

    assertEquals("abc123", filtro.id());
    assertEquals(10, filtro.numero());
    assertEquals(Seleccion.ARGENTINA, filtro.seleccion());
    assertEquals("Messi", filtro.jugador());
  }

  @Test
  void deberiaAceptarValoresNull() {
    FiguritasFiltro filtro = new FiguritasFiltro(null, null, null, null);

    assertNull(filtro.id());
    assertNull(filtro.numero());
    assertNull(filtro.seleccion());
    assertNull(filtro.jugador());
  }

  @Test
  void deberiaSerIgualSiLosValoresSonIguales() {
    FiguritasFiltro a = new FiguritasFiltro("1", 10, Seleccion.ARGENTINA, "Messi");
    FiguritasFiltro b = new FiguritasFiltro("1", 10, Seleccion.ARGENTINA, "Messi");

    assertEquals(a, b);
  }
}