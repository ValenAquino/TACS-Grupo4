package app.controllers;

import app.dto.FaltantesDto;
import app.dto.Repetidas;
import app.dto.request.FaltanteRequest;
import app.dto.request.RepetidaRequest;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import app.servicios.ServicioColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/colecciones")
@RequiredArgsConstructor
public class ControladorColeccion {

    private final ServicioColeccion coleccionService;

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
            request.figId(), request.cantidadExistente(), request.modosIntercambio());

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
    public ResponseEntity<Repetidas> buscarRepetidas(
        @PathVariable String col_id,
        @ModelAttribute RepetidasFiltro filtros
    ) {
        return ResponseEntity.ok(this.coleccionService.buscarRepetidas(col_id, filtros));
    }

}
