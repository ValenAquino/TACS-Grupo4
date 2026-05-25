package app.controllers;

import app.dto.request.ContraseniaRequest;
import app.dto.request.UsuarioRequest;
import app.model.entities.Rol;
import app.servicios.ServicioJwt;
import app.servicios.ServicioUsuario;
import jakarta.validation.Valid;
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

  @PutMapping("/usuarios/contrasenia")
  public ResponseEntity<Void> editarContrasenia(
      @CookieValue("token") String token,
      @Valid @RequestBody ContraseniaRequest body
  ) {
    String perfilId = this.servicioJwt.getPerfilId(token);
    this.servicioUsuario.editarContrasenia(perfilId, body.getContraseniaActual(), body.getContraseniaNueva());
    return ResponseEntity.ok().build();
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
