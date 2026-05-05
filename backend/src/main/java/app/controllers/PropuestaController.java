package app.controllers;

import app.dto.PropuestaDto;
import app.dto.TemporalDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.propuesta.PropuestasDto;
import app.dto.request.CrearPropuestaRequest;
import app.servicios.IPropuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/propuestas")
public class PropuestaController {
    private final IPropuestaService propuestaService;

    @PostMapping
    public ResponseEntity<PropuestaDto> crearPropuesta(@RequestBody CrearPropuestaRequest request) {
        return ResponseEntity.status(201).body(propuestaService.crearPropuesta(request));
    }

    @PatchMapping("/{prop_id}/aceptar")
    public ResponseEntity<?> aceptar(@PathVariable String prop_id,
                                     @RequestHeader String usuario_id) {
        propuestaService.aceptar(prop_id, usuario_id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{prop_id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable String prop_id,
                                      @RequestHeader String usuario_id) {
        propuestaService.rechazar(prop_id, usuario_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<PropuestasDto> obtenerPropuestas(
        @RequestParam String user_id,
        @ModelAttribute PropuestasFiltro filtros
        ) {
        return ResponseEntity.ok(this.propuestaService.buscarPropuestas(user_id, filtros));
    }
}
