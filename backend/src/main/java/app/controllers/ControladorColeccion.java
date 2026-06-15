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
import jakarta.validation.Valid;
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
        @CookieValue("token") String token,
        @Valid @RequestBody FaltanteRequest request
    ) {
        String colId = this.servicioJwt.getColeccionId(token);
        coleccionService.agregarFaltante(colId, request.getFigId());

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/repetidas")
    public ResponseEntity<Void> agregarRepetida(
        @CookieValue("token") String token,
        @Valid @RequestBody RepetidaRequest request
    ) {
        String colId = this.servicioJwt.getColeccionId(token);
        String perfilId = this.servicioJwt.getPerfilId(token);
        coleccionService.agregarRepetida(colId, perfilId,
            request.figId(), request.cantidadExistente(), request.modosIntercambio());

        return ResponseEntity.status(201).build();
    }

    @GetMapping("/faltantes")
    public ResponseEntity<PaginaResultado<FiguritaDto>> buscarFaltantes(
        @CookieValue("token") String token,
        @ModelAttribute FaltantesFiltro filtros
    ) {
        String colId = this.servicioJwt.getColeccionId(token);
        return ResponseEntity.ok(this.coleccionService.buscarFaltantes(colId, filtros));
    }

    @GetMapping("/repetidas")
    public ResponseEntity<Repetidas<FiguritaIntercambiableDto>> buscarRepetidas(
        @CookieValue("token") String token,
        @ModelAttribute RepetidasFiltro filtros
    ) {
        String colId = this.servicioJwt.getColeccionId(token);
        return ResponseEntity.ok(this.coleccionService.buscarRepetidas(colId, filtros));
    }
}
