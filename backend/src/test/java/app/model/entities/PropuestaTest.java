package app.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.exceptions.PropuestaException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropuestaTest {

  private Perfil origen;
  private Perfil destino;
  private Propuesta propuesta;

  @BeforeEach
  void setUp() {
    Usuario user = new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella");
    origen = Perfil.builder()
        .id("1").usuario(user).nombre("Origen")
        .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM,"@origen")))
        .build();

    user = new Usuario("u-2", Rol.USUARIO,"lucas", "fiscella");

    destino = Perfil.builder()
        .id("2").usuario(user).nombre("Destino")
        .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM,"@destino")))
        .build();

    propuesta = new Propuesta(
        "123",
        origen,
        destino,
        List.of(),
        null,
        new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE)))
    );
  }

  @Test
  void deberiaAceptarPropuestaPendiente() {
    propuesta.aceptar(destino);

    assertEquals(EstadoProceso.ACEPTADO, propuesta.obtenerEstadoActual().getValor());
  }

  @Test
  void deberiaRechazarPropuestaPendiente() {
    propuesta.rechazar(destino);

    assertEquals(EstadoProceso.RECHAZADO, propuesta.obtenerEstadoActual().getValor());
  }

  @Test
  void noDeberiaAceptarUnaPropuestaYaAceptada() {
    propuesta.aceptar(destino);

    assertThrows(PropuestaException.class, () -> propuesta.aceptar(destino));
  }

  @Test
  void noDeberiaRechazarUnaPropuestaYaAceptada() {
    propuesta.aceptar(destino);

    assertThrows(PropuestaException.class, () -> propuesta.rechazar(destino));
  }

  @Test
  void noDeberiaAceptarSiNoEsElUsuarioDestino() {
    Usuario user = new Usuario("u-3", Rol.USUARIO,"lucas", "fiscella");
    Perfil otro = Perfil.builder()
        .id("3").usuario(user).nombre("Otro")
        .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM,"@otro")))
        .build();

    assertThrows(PropuestaException.class, () -> propuesta.aceptar(otro));
  }

  @Test
  void noDeberiaRechazarSiNoEsElUsuarioDestino() {
    Usuario user = new Usuario("u-3", Rol.USUARIO,"lucas", "fiscella");
    Perfil otro = Perfil.builder()
        .id("3").usuario(user).nombre("Otro")
        .mediosDeContacto(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM,"@otro")))
        .build();

    assertThrows(PropuestaException.class, () -> propuesta.rechazar(otro));
  }

  @Test
  void noDeberiaAceptarUnaPropuestaYaRechazada() {
    propuesta.rechazar(destino);

    assertThrows(PropuestaException.class, () -> propuesta.aceptar(destino));
  }

  @Test
  void noDeberiaRechazarUnaPropuestaYaRechazada() {
    propuesta.rechazar(destino);

    assertThrows(PropuestaException.class, () -> propuesta.rechazar(destino));
  }
}