package app.controllers;

import app.dto.request.CrearSubastaRequest;
import app.dto.request.OfertarEnSubastaRequest;
import app.dto.subasta.MisSubastasResponseDto;
import app.dto.subasta.SubastasParticipoResponseDto;
import app.servicios.ISubastaService;
import app.dto.subasta.SubastaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequiredArgsConstructor
public class SubastaController {
    private final ISubastaService subastaService;

    @PostMapping
    public ResponseEntity<Void> crearSubasta(@RequestHeader("user_id") String id,
                                             @RequestBody CrearSubastaRequest body) {
        this.subastaService.crearSubasta(
            id,
            body.getFiguritaId(),
            body.getDuracionEnHoras(),
            body.getFiguritasDeseadasIds(),
            body.getCalificacionMinima()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sub_id}/propuestas")
    public ResponseEntity<Void> ofertarEnSubasta(@PathVariable String sub_id, @RequestHeader("user_id") String id, @RequestBody OfertarEnSubastaRequest body) {
        this.subastaService.ofertarEnSubasta(id, body.getUsuarioId(), sub_id, body.getFiguritasOfrecidasId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sub_id}/ofertas/{oferta_id}/seleccionar")
    public ResponseEntity<Void> seleccionarOferta(@PathVariable String sub_id, @PathVariable String oferta_id) {
        this.subastaService.seleccionarOferta(sub_id, oferta_id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sub_id}/ofertas/{oferta_id}/rechazar")
    public ResponseEntity<Void> rechazarOferta(@PathVariable String sub_id, @PathVariable String oferta_id) {
        this.subastaService.rechazarOferta(sub_id, oferta_id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sub_id}/cancelar")
    public ResponseEntity<Void> cancelarSubasta(@PathVariable String sub_id) {
        this.subastaService.cancelarSubasta(sub_id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sub_id}/cerrar")
    public ResponseEntity<Void> cerrarSubasta(@PathVariable String sub_id) {
        this.subastaService.cerrarSubasta(sub_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mis-subastas")
    public ResponseEntity<MisSubastasResponseDto> obtenerMisSubastas(@RequestHeader("user_id") String userId) {
        return ResponseEntity.ok(this.subastaService.obtenerMisSubastas(userId));
    }

    @GetMapping("/participo")
    public ResponseEntity<SubastasParticipoResponseDto> obtenerSubastasParticipo(@RequestHeader("user_id") String userId) {
        return ResponseEntity.ok(this.subastaService.obtenerSubastasParticipo(userId));
    }

  @GetMapping("/{sub_id}")
  public ResponseEntity<SubastaDto> obtenerSubasta(@PathVariable String sub_id) {
    SubastaDto subasta = this.subastaService.obtenerSubasta(sub_id);

    return ResponseEntity.ok().body(subasta);
  }
}