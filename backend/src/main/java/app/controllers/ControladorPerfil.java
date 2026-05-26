package app.controllers;

import app.dto.CalificacionDto;
import app.dto.ContadorDto;
import app.dto.NotificacionDto;
import app.dto.PerfilDto;
import app.dto.SugerenciaDto;
import app.dto.filtros.SugerenciasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CalificacionRequest;
import app.dto.request.PerfilRequest;
import app.servicios.ServicioJwt;
import app.servicios.ServicioPerfil;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class ControladorPerfil {

    private final ServicioPerfil perfilService;
    private final ServicioJwt servicioJwt;

    @PostMapping("/calificaciones")
    public ResponseEntity<Void> calificarPerfil(
        @CookieValue("token") String token,
        @Valid @RequestBody CalificacionRequest body
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
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
        String perfilId = this.servicioJwt.getPerfilId(token);
        return ResponseEntity.ok(this.perfilService.obtenerCalificaciones(perfilId, pagina, limite));
    }

    @GetMapping("/sugerencias")
    public ResponseEntity<PaginaResultado<SugerenciaDto>> obtenerSugerencias(
        @CookieValue String token,
        @ModelAttribute SugerenciasFiltro filtro
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
        PaginaResultado<SugerenciaDto> sugerenciasDto = this.perfilService.obtenerSugerencias(perfilId, filtro);

        return ResponseEntity.ok().body(sugerenciasDto);
    }

    @GetMapping("/contadores")
    public ResponseEntity<List<ContadorDto>> obtenerContadores(
        @CookieValue("token") String token
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
        return ResponseEntity.ok(this.perfilService.obtenerContadores(perfilId));
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionDto>> obtenerNotificaciones(
        @CookieValue String token
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
        return ResponseEntity.ok(this.perfilService.obtenerNotificaciones(perfilId));
    }

    @PatchMapping("/notificaciones/leidas")
    public ResponseEntity<Void> marcarTodasLeidas(
            @CookieValue String token
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
        this.perfilService.marcarTodasNotifsLeidas(perfilId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PerfilDto> obtenerPerfil(
        @CookieValue("token") String token
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
        return ResponseEntity.ok(this.perfilService.obtenerPerfil(perfilId));
    }

    @PutMapping
    public ResponseEntity<Void> editarPerfil(
        @CookieValue("token") String token,
        @Valid @RequestBody PerfilRequest body
    ) {
        String perfilId = this.servicioJwt.getPerfilId(token);
        this.perfilService.editarPerfil(perfilId, body);
        return ResponseEntity.ok().build();
    }

}