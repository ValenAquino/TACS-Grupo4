package app.model.notificador;

import app.model.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificacion {
  String id;
  Mensaje mensaje;
  Usuario usuario;

  public Notificacion(Mensaje mensaje, Usuario usuario) {
    this.mensaje = mensaje;
    this.usuario = usuario;
  }
}
