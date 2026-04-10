package app.controllers;

import app.dto.TemporalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/administrador")
public class AdministradorController {

    @GetMapping("/estadisticas")
    public ResponseEntity<TemporalDto> getEstadisticas() {
        return ResponseEntity.ok(new TemporalDto("GET /administrador/estadisticas"));
    }

}
