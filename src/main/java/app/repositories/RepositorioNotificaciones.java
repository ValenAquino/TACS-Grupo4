package app.repositories;

import app.model.entities.Usuario;
import app.model.notificador.Notificacion;

import java.util.List;

public interface RepositorioNotificaciones {
  void save(Notificacion notificacion);
  List<Notificacion> buscarPorUsuario(Usuario usuario);
}
