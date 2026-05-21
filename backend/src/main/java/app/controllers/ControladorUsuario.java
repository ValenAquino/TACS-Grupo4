package app.controllers;

import app.dto.request.UsuarioRequest;
import app.servicios.ServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class ControladorUsuario {

  private final ServicioUsuario servicioUsuario;

  @PostMapping()
  public ResponseEntity<Void> registrar(
      @RequestBody UsuarioRequest request
  ) {
    this.servicioUsuario.registrar(request);
    return ResponseEntity.noContent().build();
  }

}
