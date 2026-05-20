package app.controllers;

import app.dto.FiguritaDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.filtros.FaltantesFiltro;
import app.dto.filtros.RepetidasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.dto.request.FaltanteRequest;
import app.dto.request.RepetidaRequest;
import app.servicios.ServicioColeccion;
import app.servicios.ServicioJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final ServicioJwt servicioJwt;

    @PostMapping("/faltantes")
    public ResponseEntity<Void> agregarFaltante(
        @CookieValue String token,
        @RequestBody FaltanteRequest request
    ) {
        String colId = this.obtenerColeccionIdDeCookie(token);
        coleccionService.agregarFaltante(colId, request.getFigId());

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/repetidas")
    public ResponseEntity<Void> agregarRepetida(
        @CookieValue String token,
        @RequestBody RepetidaRequest request
    ) {
        String colId = this.obtenerColeccionIdDeCookie(token);
        coleccionService.agregarRepetida(colId,
            request.figId(), request.cantidadExistente(), request.modosIntercambio());

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/faltantes")
    public ResponseEntity<PaginaResultado<FiguritaDto>> buscarFaltantes(
        @CookieValue String token,
        @ModelAttribute FaltantesFiltro filtros
    ) {
        String colId = this.obtenerColeccionIdDeCookie(token);
        return ResponseEntity.ok(this.coleccionService.buscarFaltantes(colId, filtros));
    }

    @GetMapping("/repetidas")
    public ResponseEntity<Repetidas<FiguritaIntercambiableDto>> buscarRepetidas(
        @CookieValue String token,
        @ModelAttribute RepetidasFiltro filtros
    ) {
        String colId = this.obtenerColeccionIdDeCookie(token);
        return ResponseEntity.ok(this.coleccionService.buscarRepetidas(colId, filtros));
    }

    private String obtenerColeccionIdDeCookie(String token) {
        return this.servicioJwt.getColeccionId(token);
    }
}
