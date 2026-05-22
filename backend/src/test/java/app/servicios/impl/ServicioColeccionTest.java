package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import app.MongoTestBase;
import app.exceptions.BadRequestException;
import app.model.entities.*;
import java.util.List;

import app.repositories.impl.campos.CamposColeccion;
import app.servicios.ServicioColeccion;
import app.servicios.ServicioNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ServicioColeccionTest extends MongoTestBase {

  @Autowired
  private ServicioNotificacion notificacionService;

  private ServicioColeccion service;

  private Figurita messi;
  private Coleccion coleccion;

  @BeforeEach
  void setUp() {
    service = new ServicioColeccion(repositorioFiguritas, repositorioColecciones,
        repositorioPerfiles, notificacionService);

    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);

    repositorioFiguritas.guardar(messi);

    coleccion = new Coleccion();
    repositorioColecciones.guardar(coleccion);
  }

  @Test
  void agregarFaltante_agregaFiguritaAColeccion() {
    service.agregarFaltante(coleccion.getId(), "ARG-10");

    assertEquals(1, repositorioColecciones.buscarPorId(coleccion.getId(), new CamposColeccion(false, false)).getFaltantes().size());
    assertEquals(messi.getId(), repositorioColecciones.buscarPorId(coleccion.getId(), new CamposColeccion(false, false)).getFaltantes().get(0).getId());
  }

  @Test
  void agregarFaltante_figuritaDuplicada_lanzaExcepcion() {
    coleccion.agregarFaltante(messi);
    repositorioColecciones.guardar(coleccion);

    assertThrows(BadRequestException.class,
        () -> service.agregarFaltante(coleccion.getId(), "ARG-10"));
  }

  @Test
  void agregarRepetida_agregaFiguritaYNotificaInteresados() {
    Usuario user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    repositorioUsuarios.guardar(user);
    Coleccion coleccion2 = new Coleccion();
    coleccion2.agregarFaltante(messi);
    repositorioColecciones.guardar(coleccion2);
    Perfil interesado = Perfil.builder()
        .id("2").usuario(user).nombre("Sofía")
        .coleccion(coleccion2)
        .build();
    repositorioPerfiles.guardar(interesado);

    service.agregarRepetida(this.coleccion.getId(),  "ARG-10", 2, List.of(MetodoIntercambio.INTERCAMBIO));

    Coleccion coleccion = repositorioColecciones.buscarPorId(this.coleccion.getId(), new CamposColeccion(true, false));

    assertEquals(1, coleccion.getRepetidas().size());
    assertEquals(1, repositorioNotificaciones.buscarPorPerfil(interesado).size());
  }

  @Test
  void agregarRepetida_sinInteresados_noNotifica() {
    service.agregarRepetida(this.coleccion.getId(), "ARG-10", 2, List.of(MetodoIntercambio.INTERCAMBIO));

    repositorioPerfiles.buscarTodos().forEach(p -> assertEquals(0, repositorioNotificaciones.buscarPorPerfil(p).size()));
  }
}