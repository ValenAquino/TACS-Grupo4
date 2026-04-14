package app.controllers;

import app.dto.FiguritaIntercambiableDto;
import app.dto.OperacionesDto;
import app.dto.TemporalDto;
import app.model.notificador.Notificacion;
import app.servicios.UsuarioService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{user_id}/operaciones")
    public ResponseEntity<OperacionesDto> getOperaciones(@PathVariable String user_id) {
        OperacionesDto operaciones = usuarioService.getOperacionesUsuario(user_id);
        if (operaciones == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(operaciones);
    }

    @PostMapping("/{user_id}/calificaciones")
    public ResponseEntity<TemporalDto> calificarUsuario(@PathVariable String user_id) {
        return ResponseEntity.ok(new TemporalDto("POST /usuarios/" + user_id + "/calificaciones"));
    }

    @GetMapping("/{user_id}/intercambiables")
    public ResponseEntity<List<FiguritaIntercambiableDto>> getIntercambiables(
        @PathVariable String user_id) {
        return ResponseEntity.ok(usuarioService.getIntercambiablesUsuario(user_id));
    }
    @GetMapping("/{user_id}/notificaciones")
    public ResponseEntity<List<Notificacion>> getNotificaciones(@PathVariable String user_id) {
        return ResponseEntity.ok(this.usuarioService.getNotificaciones(user_id));

    }
}
