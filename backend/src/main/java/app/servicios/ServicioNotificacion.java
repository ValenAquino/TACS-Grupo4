package app.servicios;

import app.model.entities.Perfil;
import app.model.notificador.Mensaje;
import app.model.notificador.Notificacion;
import app.repositories.RepositorioNotificaciones;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ServicioNotificacion {

  private final RepositorioNotificaciones repositorioNotificaciones;

  public void  notificarInteresados(List<Perfil> interesados, String cuerpo) {
    interesados.forEach(u -> {
      Mensaje mensaje = new Mensaje(cuerpo, LocalDateTime.now());
      Notificacion noti = new Notificacion(mensaje, u);
      this.repositorioNotificaciones.guardar(noti);
    });
  }
}
