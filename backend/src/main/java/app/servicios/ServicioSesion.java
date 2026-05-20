package app.servicios;

import app.dto.request.UsuarioRequest;
import app.model.entities.Usuario;
import app.repositories.RepositorioUsuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioSesion {
  private final RepositorioUsuarios repoSesion;

  public void crearUsuario(UsuarioRequest req) {
    Usuario usuario = new Usuario(null, req.getRol(), req.getNombre(), req.getContrasenia());

    this.repoSesion.guardar(usuario);
  }
}
