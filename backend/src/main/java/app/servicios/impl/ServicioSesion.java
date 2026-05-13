package app.servicios.impl;

import app.dto.request.UsuarioRequest;
import app.model.entities.Usuario;
import app.repositories.RepositorioUsuario;
import app.servicios.IServicioSesion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioSesion implements IServicioSesion {
  private final RepositorioUsuario repoSesion;

  public void crearUsuario(UsuarioRequest req) {
    Usuario usuario = new Usuario(null, req.getRol(), req.getNombre(), req.getContrasenia());

    this.repoSesion.guardar(usuario);
  }
}
