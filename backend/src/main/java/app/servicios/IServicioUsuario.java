package app.servicios;

import app.dto.request.UsuarioRequest;
import app.model.entities.Rol;

public interface IServicioUsuario {

  public void registrar(UsuarioRequest request);
}
