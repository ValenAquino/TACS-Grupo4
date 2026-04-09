package app.model.notificador;

import app.model.entities.Usuario;

public interface AdapterNotificacion {
  public void notificar(Mensaje mensaje, Usuario receptor);
}
