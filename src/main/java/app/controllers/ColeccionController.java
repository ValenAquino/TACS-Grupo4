package app.controllers;

import app.dto.TemporalDto;
import app.dto.request.FaltanteRequest;
import app.dto.request.RepetidaRequest;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.servicios.ColeccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coleccion")
public class ColeccionController {

    private final ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    @PostMapping("/{col_id}/repetidas")
    public ResponseEntity<TemporalDto> agregarRepetida(
        @PathVariable String col_id,
        @RequestBody RepetidaRequest request) {

        FiguritaIntercambiable repetida = coleccionService.agregarRepetida(col_id,
            request.getFigId(), request.getCantidadDisponible(), request.getModosIntercambio());

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/{col_id}/faltantes")
    public ResponseEntity<Figurita> agregarFaltante(
        @PathVariable String col_id,
        @RequestBody FaltanteRequest request) {

        Figurita faltante = coleccionService.agregarFaltante(col_id, request.getFigId());

        return ResponseEntity.status(201).build();
    }


}
