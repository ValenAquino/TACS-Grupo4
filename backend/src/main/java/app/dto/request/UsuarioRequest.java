package app.dto.request;

import app.model.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioRequest {
  String nombre;
  String contrasenia;
  Rol rol;
}
