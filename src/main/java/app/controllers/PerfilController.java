package app.controllers;

import app.dto.FiguritaIntercambiableDto;
import app.dto.NotificacionesDto;
import app.dto.OperacionesDto;
import app.dto.SugerenciaDto;
import app.dto.TemporalDto;
import app.servicios.IPerfilService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final IPerfilService perfilService;

    public PerfilController(IPerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/{user_id}/operaciones")
    public ResponseEntity<OperacionesDto> obtenerOperaciones(@PathVariable String user_id) {
        OperacionesDto operaciones = perfilService.obtenerOperacionesPerfil(user_id);
        if (operaciones == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(operaciones);
    }

    @PostMapping("/{perfil_id}/calificaciones")
    public ResponseEntity<TemporalDto> calificarPerfil(
        @PathVariable String perfil_id,
        @RequestHeader String autor_id,
        @RequestBody Map<String, Object> body) {

        Number calificacionMedia = this.perfilService.agregarCalificacion(
            autor_id,
            perfil_id,
            (Integer) body.get("valor"),
            (String) body.get("descripcion")
        );

        return ResponseEntity.ok(new TemporalDto("Nueva calificacion: " + calificacionMedia));
    }

    @GetMapping("/{user_id}/intercambiables")
    public ResponseEntity<List<FiguritaIntercambiableDto>> obtenerIntercambiables(
        @PathVariable String user_id) {

        return ResponseEntity.ok(
            perfilService.obtenerIntercambiablesPerfil(user_id)
        );
    }
    @GetMapping("/{user_id}/sugerencias")
    public ResponseEntity<List<SugerenciaDto>> obtenerSugerencias(@PathVariable String user_id) {
        List<SugerenciaDto> sugerenciasDto = this.perfilService.obtenerSugerencias(user_id);

        return ResponseEntity.accepted().body(sugerenciasDto);
    }
    @GetMapping("/{user_id}/notificaciones")
    public ResponseEntity<List<NotificacionesDto>> obtenerNotificaciones(@PathVariable String user_id) {
        return ResponseEntity.ok(this.perfilService.obtenerNotificaciones(user_id));
    }
}