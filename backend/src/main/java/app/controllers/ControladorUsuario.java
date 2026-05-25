package app.controllers;

import app.dto.request.UsuarioRequest;
import app.model.entities.Rol;
import app.servicios.ServicioJwt;
import app.servicios.ServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ControladorUsuario {

  private final ServicioUsuario servicioUsuario;
  private final ServicioJwt servicioJwt;

  @PostMapping("/usuarios")
  public ResponseEntity<Void> registrarUsuario(
      @RequestBody UsuarioRequest request
  ) {
    this.servicioUsuario.registrarUsuario(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/administradores")
  public ResponseEntity<Void> registrarAdministrador(
      @CookieValue("token") String token,
      @RequestBody UsuarioRequest request
  ) {
    this.servicioUsuario.registrarAdministrador(request, Rol.valueOf(this.servicioJwt.getRol(token)));
    return ResponseEntity.noContent().build();
  }
}
