package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.MongoTestBase;
import app.exceptions.FiguritaDuplicadaException;
import app.model.entities.*;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import java.util.List;

import app.servicios.ServicioColeccion;
import app.servicios.ServicioNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
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

    coleccion = new Coleccion("col-1");
    repositorioColecciones.guardar(coleccion);
  }

  @Test
  void agregarFaltante_agregaFiguritaAColeccion() {
    service.agregarFaltante("col-1", "ARG-10");

    assertEquals(1, repositorioColecciones.buscarPorId("col-1").getFaltantes().size());
    assertEquals(messi.getId(), repositorioColecciones.buscarPorId("col-1").getFaltantes().get(0).getId());
  }

  @Test
  void agregarFaltante_figuritaDuplicada_lanzaExcepcion() {
    coleccion.agregarFaltante(messi);
    repositorioColecciones.guardar(coleccion);

    assertThrows(FiguritaDuplicadaException.class,
        () -> service.agregarFaltante("col-1", "ARG-10"));
  }

  @Test
  void agregarRepetida_agregaFiguritaYNotificaInteresados() {
    Usuario user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    repositorioUsuarios.guardar(user);
    Coleccion coleccion2 = new Coleccion("col-2");
    coleccion2.agregarFaltante(messi);
    repositorioColecciones.guardar(coleccion2);
    Perfil interesado = Perfil.builder()
        .id("2").usuario(user).nombre("Sofía")
        .coleccion(coleccion2)
        .build();
    repositorioPerfiles.guardar(interesado);

    service.agregarRepetida("col-1",  "ARG-10", 2, List.of(MetodoIntercambio.INTERCAMBIO));

    Coleccion coleccion = repositorioColecciones.buscarPorId("col-1");

    assertEquals(1, coleccion.getRepetidas().size());
    assertEquals(1, repositorioNotificaciones.buscarPorPerfil(interesado).size());
  }

  @Test
  void agregarRepetida_sinInteresados_noNotifica() {
    service.agregarRepetida("col-1", "ARG-10", 2, List.of(MetodoIntercambio.INTERCAMBIO));

    repositorioPerfiles.buscarTodos().forEach(p -> assertEquals(0, repositorioNotificaciones.buscarPorPerfil(p).size()));
  }
}