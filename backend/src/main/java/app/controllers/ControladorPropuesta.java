package app.controllers;

import app.dto.IntercambioDto;
import app.dto.PropuestaDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CrearPropuestaRequest;
import app.servicios.ServicioJwt;
import app.servicios.ServicioPropuesta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/propuestas")
public class ControladorPropuesta {
    private final ServicioPropuesta propuestaService;
    private final ServicioJwt servicioJwt;

    @PostMapping
    public ResponseEntity<PropuestaDto> crearPropuesta(
        @CookieValue("token") String token,
        @Valid @RequestBody CrearPropuestaRequest request
    ) {
        String autorId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.status(201).body(propuestaService.crearPropuesta(autorId, request));
    }

    @GetMapping("/{prop_id}")
    public ResponseEntity<PropuestaDto> obtenerPropuesta(
            @CookieValue String token,
            @PathVariable String prop_id
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.propuestaService.obtenerPorId(prop_id, perfilId));
    }

    @PatchMapping("/{prop_id}/aceptar")
    public ResponseEntity<?> aceptar(
        @CookieValue String token,
        @PathVariable String prop_id
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        propuestaService.aceptar(prop_id, perfilId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{prop_id}/rechazar")
    public ResponseEntity<?> rechazar(
        @CookieValue String token,
        @PathVariable String prop_id
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        propuestaService.rechazar(prop_id, perfilId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{prop_id}/cancelar")
    public ResponseEntity<?> cancelar(
        @CookieValue String token,
        @PathVariable String prop_id
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        propuestaService.cancelar(prop_id, perfilId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<PaginaResultado<IntercambioDto>> obtenerPropuestas(
        @CookieValue String token,
        @ModelAttribute PropuestasFiltro filtros
    ) {
        String perfilId = this.obtenerPerfilIdDeCookie(token);
        return ResponseEntity.ok(this.propuestaService.buscarPropuestas(perfilId, filtros));
    }

    private String obtenerPerfilIdDeCookie(String token) {
        return this.servicioJwt.getPerfilId(token);
    }
}
