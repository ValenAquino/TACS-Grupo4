package app.controllers;

import app.dto.EstadisticasDto;
import app.dto.SesionDto;
import app.dto.request.LoginRequest;
import app.servicios.ServicioEstadisticas;
import app.servicios.ServicioJwt;
import app.servicios.ServicioSesion;
import jakarta.servlet.http.HttpServletResponse;
import app.dto.PerfilDto;
import app.dto.request.UsuarioRequest;
import app.servicios.ServicioEstadisticas;
import app.servicios.ServicioSesion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControladorSesion {

    private final ServicioEstadisticas estadisticasService;
    private final ServicioSesion servicioSesion;
    private final ServicioJwt servicioJwt;


    @GetMapping("/administrador/estadisticas")
    public ResponseEntity<EstadisticasDto> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
        @RequestBody LoginRequest request,
        HttpServletResponse response
    ) {
        String token = this.servicioSesion.login(request);

        ResponseCookie cookie =
            ResponseCookie.from("token", token)
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
        @CookieValue("token") String token
    ) {
        SesionDto dto = this.servicioJwt.obtenerSesion(token);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/sesion")
    public ResponseEntity<Void> cerrarSesion(
        HttpServletResponse response
    ) {

        ResponseCookie cookie =
            ResponseCookie.from("token", "")
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
