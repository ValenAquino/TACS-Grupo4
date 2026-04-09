package app.model.notificador;

import app.model.entities.Usuario;

public class Notificador {
  public AdapterNotificacion metodo;

  Notificador(AdapterNotificacion metodo) {
    this.metodo = metodo;
  }

  public void enviarNotificacion(Mensaje mensaje, Usuario usuario) {
    this.metodo.notificar(mensaje, usuario);
  }
}
