package app.controllers;

import app.dto.TemporalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coleccion")
public class ColeccionController {

    @PostMapping("/{col_id}/repetidas")
    public ResponseEntity<TemporalDto> agregarRepetida(@PathVariable String col_id) {
        return ResponseEntity.ok(new TemporalDto("POST /coleccion/" + col_id + "/repetidas"));
    }

    @PostMapping("/{col_id}/faltantes")
    public ResponseEntity<TemporalDto> agregarFaltante(@PathVariable String col_id) {
        return ResponseEntity.ok(new TemporalDto("POST /coleccion/" + col_id + "/faltantes"));
    }

}
