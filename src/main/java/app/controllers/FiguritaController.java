package app.controllers;

import app.dto.TemporalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FiguritaController {

    @GetMapping("/figuritas")
    public ResponseEntity<TemporalDto> getFiguritas() {
        return ResponseEntity.ok(new TemporalDto("GET /figuritas"));
    }
}
