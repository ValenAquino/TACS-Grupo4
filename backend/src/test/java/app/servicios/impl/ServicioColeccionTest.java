package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ServicioColeccionTest {

  @Mock
  private RepositorioColecciones repositorioColecciones;
  @Mock private RepositorioFiguritas repositorioFiguritas;
  @Mock private RepositorioPerfiles repositorioPerfiles;
  @Mock private ServicioNotificacion notificacionService;

  private ServicioColeccion service;

  private Figurita messi;
  private Coleccion coleccion;

  @BeforeEach
  void setUp() {
    service = new ServicioColeccion(repositorioFiguritas, repositorioColecciones,
        repositorioPerfiles, notificacionService);

    messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);

    coleccion = new Coleccion();
    coleccion.setId("col-1");
  }

  @Test
  void agregarFaltante_agregaFiguritaAColeccion() {
    when(repositorioColecciones.buscarPorId("col-1")).thenReturn(coleccion);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);

    service.agregarFaltante("col-1", "ARG-10");

    assertEquals(1, coleccion.getFaltantes().size());
    assertEquals(messi, coleccion.getFaltantes().get(0));
    verify(repositorioColecciones).guardar(coleccion);
  }

  @Test
  void agregarFaltante_figuritaDuplicada_lanzaExcepcion() {
    coleccion.getFaltantes().add(messi);

    when(repositorioColecciones.buscarPorId("col-1")).thenReturn(coleccion);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);

    assertThrows(FiguritaDuplicadaException.class,
        () -> service.agregarFaltante("col-1", "ARG-10"));
  }

  @Test
  void agregarRepetida_agregaFiguritaYNotificaInteresados() {
    Usuario user = new Usuario("u-2", Rol.USUARIO, "lucas", "fiscella");
    Perfil interesado = Perfil.builder()
        .id("2").usuario(user).nombre("Sofía")
        .build();

    when(repositorioColecciones.buscarPorId("col-1")).thenReturn(coleccion);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);
    when(repositorioPerfiles.buscarPorFiguritaFaltante(messi)).thenReturn(List.of(interesado));

    service.agregarRepetida("col-1",  "ARG-10", 2, List.of(MetodoIntercambio.INTERCAMBIO));

    assertEquals(1, coleccion.getRepetidas().size());
    verify(repositorioColecciones).guardar(coleccion);
    verify(notificacionService).notificarInteresados(eq(List.of(interesado)), anyString());
  }

  @Test
  void agregarRepetida_sinInteresados_noNotifica() {
    when(repositorioColecciones.buscarPorId("col-1")).thenReturn(coleccion);
    when(repositorioFiguritas.buscarPorId("ARG-10")).thenReturn(messi);
    when(repositorioPerfiles.buscarPorFiguritaFaltante(messi)).thenReturn(List.of());

    service.agregarRepetida("col-1", "ARG-10", 2, List.of(MetodoIntercambio.INTERCAMBIO));

    verify(notificacionService).notificarInteresados(eq(List.of()), anyString());
  }
}