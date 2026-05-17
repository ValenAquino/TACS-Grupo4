package app.controllers;

import app.dto.request.UsuarioRequest;
import app.servicios.ServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControladorUsuario {

  private final ServicioUsuario servicioUsuario;

  @PostMapping("/registrar")
  public ResponseEntity<Void> registrar(@RequestBody UsuarioRequest request) {
    this.servicioUsuario.registrar(request);
    return ResponseEntity.noContent().build();
  }
}
