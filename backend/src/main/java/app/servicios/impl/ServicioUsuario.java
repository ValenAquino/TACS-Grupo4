package app.servicios.impl;

import app.dto.request.LoginRequest;
import app.dto.request.UsuarioRequest;
import app.exceptions.UsuarioException;
import app.model.entities.Coleccion;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioUsuario;
import app.servicios.IServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ServicioUsuario implements IServicioUsuario {

  private final RepositorioUsuario repositorioUsuario;
  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioColecciones repositorioColecciones;
  private final ServicioJwt servicioJwt;

  public void registrar(UsuarioRequest request) {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Usuario usuarioNuevo = new Usuario(request.getNombre(), passwordEncoder.encode(request.getContrasenia()), request.getRol());

    this.repositorioUsuario.guardar(usuarioNuevo);

    Coleccion coleccion = new Coleccion();

    this.repositorioColecciones.guardar(coleccion);

    Perfil perfil = new Perfil(usuarioNuevo, usuarioNuevo.getNombre(), coleccion, new ArrayList<>(), new ArrayList<>());

    this.repositorioPerfiles.guardar(perfil);
  }

  public String login(LoginRequest request) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    Usuario usuario = this.repositorioUsuario.buscarPorNombre(request.nombre());

    boolean coincide = passwordEncoder.matches(request.contrasenia(), usuario.getContrasenia());

    if(!coincide) {
      throw new UsuarioException("Credenciales invalidas");
    }

    Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(usuario.getId());

    return this.servicioJwt.generarToken(usuario, perfil);
  }
}


