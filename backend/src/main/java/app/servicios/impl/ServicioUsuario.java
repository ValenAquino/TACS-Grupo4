package app.servicios.impl;

import app.dto.request.UsuarioRequest;
import app.model.entities.Usuario;
import app.repositories.RepositorioUsuario;
import app.servicios.IServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioUsuario implements IServicioUsuario {

  private final RepositorioUsuario repositorioUsuario;

  public void registrar(UsuarioRequest request) {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Usuario usuarioNuevo = new Usuario(request.getNombre(), passwordEncoder.encode(request.getContrasenia()), request.getRol());

    this.repositorioUsuario.guardar(usuarioNuevo);
  }
}
