package app.servicios.impl;

import app.model.entities.Perfil;
import app.model.notificador.Notificacion;
import app.repositories.RepositorioNotificaciones;
import app.servicios.ServicioNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicioNotificacionTest {

  private RepositorioNotificaciones repositorioNotificaciones;
  private ServicioNotificacion servicioNotificacion;

  @BeforeEach
  void setUp() {
    repositorioNotificaciones = mock(RepositorioNotificaciones.class);
    servicioNotificacion = new ServicioNotificacion(repositorioNotificaciones);
  }

  @Test
  void notificarInteresados_guardaUnaNotificacionPorCadaPerfil() {

    Perfil p1 = mock(Perfil.class);
    Perfil p2 = mock(Perfil.class);
    Perfil p3 = mock(Perfil.class);

    List<Perfil> interesados = List.of(p1, p2, p3);

    String cuerpo = "Nuevo evento disponible";

    servicioNotificacion.notificarInteresados(interesados, cuerpo);

    verify(repositorioNotificaciones, times(3))
        .save(any(Notificacion.class));
  }

  @Test
  void notificarInteresados_listaVacia_noGuardaNada() {

    servicioNotificacion.notificarInteresados(List.of(), "mensaje");

    verify(repositorioNotificaciones, never())
        .save(any(Notificacion.class));
  }
}
