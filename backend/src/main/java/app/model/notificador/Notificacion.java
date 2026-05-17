package app.model.notificador;

import app.model.entities.Perfil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@Document(collection = "notificaciones")
public class Notificacion {
  @Id
  String id;
  Mensaje mensaje;
  @DBRef
  Perfil usuario;

  public Notificacion(Mensaje mensaje, Perfil usuario) {
    this.mensaje = mensaje;
    this.usuario = usuario;
  }
}
