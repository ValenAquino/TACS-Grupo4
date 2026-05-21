package app.controllers;

import app.dto.*;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CalificacionRequest;
import java.util.List;
import app.servicios.ServicioJwt;
import app.servicios.ServicioPerfil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        @CookieValue("token") String token,
        @RequestBody CalificacionRequest body
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);

        this.perfilService.agregarCalificacion(
            perfilId,
            body.getDestinatarioId(),
            body.getValor(),
            body.getDescripcion(),
            body.getTransactionId(),
            body.getTipoTransaccion()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/calificaciones")
    public ResponseEntity<PaginaResultado<CalificacionDto>> obtenerCalificaciones(
        @CookieValue("token") String token,
        @RequestParam Integer pagina,
        @RequestParam Integer limite

    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.perfilService.obtenerCalificaciones(perfilId, pagina, limite));
    }

//    @GetMapping("/sugerencias")
//    public ResponseEntity<PaginaResultado<SugerenciaDto>> obtenerSugerencias(
//        @CookieValue String token,
//        @ModelAttribute SugerenciasFiltro filtro
//    ) {
//        String perfilId = this.obtenerPerfilIdDeCookie(token);
//        PaginaResultado<SugerenciaDto> sugerenciasDto = this.perfilService.obtenerSugerencias(user_id, filtro);
//
//        return ResponseEntity.ok().body(sugerenciasDto);
//    }

    @GetMapping("/contadores")
    public ResponseEntity<List<ContadorDto>> obtenerContadores(
        @CookieValue("token") String token
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

    @GetMapping
    public ResponseEntity<PerfilDto> obtenerPerfil(
        @CookieValue("token") String token
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.perfilService.obtenerPerfil(perfilId));
    }

    private String obtenerPerfilIdDeCookie(String token) {
        return this.servicioJwt.getPerfilId(token);
    }
}