package app.controllers;

import app.dto.request.UsuarioRequest;
import app.model.entities.Rol;
import app.servicios.IServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControladorUsuario {

  private final IServicioUsuario servicioUsuario;

  @PostMapping("/registrar")
  public ResponseEntity<Void> registrar(@RequestBody UsuarioRequest request) {
    this.servicioUsuario.registrar(request);
    return ResponseEntity.noContent().build();
  }
}
