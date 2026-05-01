package app.controllers;

import app.dto.FaltantesDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.RepetidasDto;
import app.dto.request.FaltanteRequest;
import app.dto.request.RepetidaRequest;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import app.servicios.IColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ColeccionController {

    private final IColeccionService coleccionService;

    @PostMapping("/{col_id}/faltantes")
    public ResponseEntity<Void> agregarFaltante(
        @PathVariable String col_id,
        @RequestBody FaltanteRequest request) {

        coleccionService.agregarFaltante(col_id, request.getFigId());

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/{col_id}/repetidas")
    public ResponseEntity<Void> agregarRepetida(
        @PathVariable String col_id,
        @RequestBody RepetidaRequest request) {

        coleccionService.agregarRepetida(col_id,
            request.figId(), request.cantidadExistente(), request.modoIntercambio());

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{col_id}/faltantes")
    public ResponseEntity<FaltantesDto> buscarFaltantes(
        @PathVariable String col_id,
        @ModelAttribute FaltantesFiltro filtros
    ) {
        return ResponseEntity.ok(this.coleccionService.buscarFaltantes(col_id, filtros));
    }

    @GetMapping("/{col_id}/repetidas")
    public ResponseEntity<RepetidasDto> buscarRepetidas(
        @PathVariable String col_id,
        @ModelAttribute RepetidasFiltro filtros
    ) {
        return ResponseEntity.ok(this.coleccionService.buscarRepetidas(col_id, filtros));
    }

}
