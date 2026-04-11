package app.controllers;

import app.dto.TemporalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subastas")
public class SubastaController {

    @PostMapping
    public ResponseEntity<TemporalDto> crearSubasta() {
        return ResponseEntity.ok(new TemporalDto("POST /subastas"));
    }

    @PostMapping("/{sub_id}/propuestas")
    public ResponseEntity<TemporalDto> ofertarEnSubasta(@PathVariable String sub_id) {
        return ResponseEntity.ok(new TemporalDto("POST /subastas/" + sub_id + "/propuestas"));
    }

}
