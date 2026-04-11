package app.controllers;

import app.dto.TemporalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping("/{user_id}/operaciones")
    public ResponseEntity<TemporalDto> getOperaciones(@PathVariable String user_id) {
        return ResponseEntity.ok(new TemporalDto("GET /usuarios/" + user_id + "/operaciones"));
    }

    @PostMapping("/{user_id}/calificaciones")
    public ResponseEntity<TemporalDto> calificarUsuario(@PathVariable String user_id) {
        return ResponseEntity.ok(new TemporalDto("POST /usuarios/" + user_id + "/calificaciones"));
    }

}
