package app.servicios.impl;

import app.model.entities.Perfil;
import app.model.notificador.Mensaje;
import app.model.notificador.Notificacion;
import app.repositories.RepositorioNotificaciones;
import app.servicios.IServicioNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Service
public class ServicioNotificacion implements IServicioNotificacion {

  private final RepositorioNotificaciones repositorioNotificaciones;

  @Override
  public void notificarInteresados(List<Perfil> interesados, String cuerpo) {
    interesados.forEach(u -> {
      Mensaje mensaje = new Mensaje(cuerpo, LocalDateTime.now());
      this.repositorioNotificaciones.guardar(new Notificacion(mensaje, u));
    });
  }
}
