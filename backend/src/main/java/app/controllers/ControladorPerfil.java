package app.controllers;

import app.dto.ContadorDto;
import app.dto.NotificacionesDto;
import app.dto.PerfilDto;
import app.dto.SugerenciaPaginadaDto;
import app.dto.calificaciones.CalificacionesDto;
import app.dto.filtros.SugerenciasFiltro;
import app.dto.request.CalificacionRequest;
import java.util.List;

import app.servicios.ServicioJwt;
import app.servicios.ServicioPerfil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class ControladorPerfil {

    private final ServicioPerfil perfilService;
    private final ServicioJwt servicioJwt;

    @PostMapping("/calificaciones")
    public ResponseEntity<Void> calificarPerfil(
        @CookieValue String token,
        @RequestBody CalificacionRequest body
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);

        this.perfilService.agregarCalificacion(
            body.getUserId(),
            perfilId,
            body.getValor(),
            body.getDescripcion(),
            body.getTransactionId(),
            body.getTipoTransaccion()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/calificaciones")
    public CalificacionesDto obtenerCalificaciones(
        @CookieValue String token,
        @RequestParam Integer pagina,
        @RequestParam Integer limite

    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return this.perfilService.obtenerCalificaciones(perfilId, pagina, limite);
    }

    @GetMapping("/sugerencias")
    public ResponseEntity<SugerenciaPaginadaDto> obtenerSugerencias(
        @CookieValue String token,
        @ModelAttribute SugerenciasFiltro filtro
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        SugerenciaPaginadaDto sugerenciasDto = this.perfilService.obtenerSugerencias(perfilId, filtro);

        return ResponseEntity.ok().body(sugerenciasDto);
    }

    @GetMapping("/contadores")
    public ResponseEntity<List<ContadorDto>> obtenerContadores(
        @CookieValue String token
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.perfilService.obtenerContadores(perfilId));
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionesDto>> obtenerNotificaciones(
        @CookieValue String token
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.perfilService.obtenerNotificaciones(perfilId));
    }

    @GetMapping()
    public ResponseEntity<PerfilDto> obtenerPerfil(
        @CookieValue String token
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.perfilService.obtenerPerfil(perfilId));
    }

    private String obtenerPerfilIdDeCookie(String token) {
        return this.servicioJwt.getPerfilId(token);
    }
}