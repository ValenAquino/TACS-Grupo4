package app.servicios;

import app.dto.request.UsuarioRequest;

public interface IServicioSesion {

  void crearUsuario(UsuarioRequest request);
}
