package app.controllers;

import app.dto.TemporalDto;
import app.model.entities.Subasta;
import app.servicios.ISubastaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subastas")
public class SubastaController {
    private ISubastaService subastaService;

    SubastaController(ISubastaService subastaService) {
        this.subastaService = subastaService;
    }

    @PostMapping
    public ResponseEntity<TemporalDto> crearSubasta(@RequestHeader("id") String id, @RequestBody Map<String,Object> body) {
        try {
            String figuritaId = (String) body.get("figuritaId");
            LocalDateTime fechaInicio =  LocalDateTime.now();
            Number duracion = (Number) body.get("duracion");
            LocalDateTime fechaFin = fechaInicio.plusMinutes(duracion.longValue());

            Subasta nuevaSubasta = this.subastaService.crearSubasta(id, fechaInicio, fechaFin, figuritaId, null);

            return ResponseEntity.ok(new TemporalDto("POST /subastas, Id: " + nuevaSubasta.getId() + " (Sin DB no hay ID)"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new TemporalDto("Bad request: " + e.getMessage()));
        }
    }

    @PostMapping("/{sub_id}/propuestas")
    public ResponseEntity<TemporalDto> ofertarEnSubasta(@PathVariable String sub_id, @RequestHeader("id") String id, @RequestBody Map<String,Object> body) {
        try{
            String usuarioDestino = (String) body.get("usuarioId");
            List<Object> rawFiguritasId = (ArrayList<Object>) body.get("figuritasOfrecidas");

            boolean esGanadora = this.subastaService.ofertarEnSubasta(id, usuarioDestino, sub_id, rawFiguritasId);

            return ResponseEntity.ok(new TemporalDto("POST /subastas/" + sub_id + "/propuestas, tu propuesta es: " + (esGanadora ? "ganadora" : "perdedora")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new TemporalDto("Bad request: "+ e.getMessage()));
        }
    }
}
