package app.controllers;

import app.dto.SesionDto;
import app.dto.request.LoginRequest;
import app.dto.request.UsuarioRequest;
import app.servicios.ServicioJwt;
import app.servicios.ServicioUsuario;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class ControladorUsuario {

  private final ServicioUsuario servicioUsuario;
  private final ServicioJwt servicioJwt;

  @PostMapping("/registrar")
  public ResponseEntity<Void> registrar(@RequestBody UsuarioRequest request) {
    this.servicioUsuario.registrar(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(
      @RequestBody LoginRequest request,
      HttpServletResponse response
  ) {
    String token = this.servicioUsuario.login(request);

    ResponseCookie cookie =
        ResponseCookie.from("sesion", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(Duration.ofHours(12))
            .build();

    response.addHeader(
        HttpHeaders.SET_COOKIE,
        cookie.toString()
    );

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/yo")
  public ResponseEntity<SesionDto> buscarUsuario(
      @CookieValue("sesion") String token
  ) {
    SesionDto dto = this.servicioJwt.obtenerSesion(token);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/sesion")
  public ResponseEntity<Void> cerrarSesion(
      HttpServletResponse response
  ) {

    ResponseCookie cookie =
        ResponseCookie.from("sesion", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build();

    response.addHeader(
        HttpHeaders.SET_COOKIE,
        cookie.toString()
    );

    return ResponseEntity.noContent().build();
  }
}
