package app.servicios;

import app.dto.request.UsuarioRequest;
import app.model.entities.Usuario;
import app.repositories.RepositorioUsuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioUsuario {

  private final RepositorioUsuarios repositorioUsuario;

  public void registrar(UsuarioRequest request) {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Usuario usuarioNuevo = new Usuario(request.getNombre(), passwordEncoder.encode(request.getContrasenia()), request.getRol());

    this.repositorioUsuario.guardar(usuarioNuevo);
  }
}
