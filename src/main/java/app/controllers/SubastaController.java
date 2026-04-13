package app.controllers;

import app.dto.TemporalDto;
import app.model.entities.Figurita;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import app.model.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/subastas")
public class SubastaController {
    private RepositorioUsuarios repoUser;
    private RepositorioSubastas repoSubasta;
    private RepositorioFiguritas repoFigurita;

    SubastaController(RepositorioUsuarios repoUser, RepositorioSubastas repoSubasta, RepositorioFiguritas repoFigurita) {
        this.repoUser = repoUser;
        this.repoSubasta = repoSubasta;
        this.repoFigurita = repoFigurita;
    }

    @PostMapping
    public ResponseEntity<TemporalDto> crearSubasta(@RequestHeader("id") String id, @RequestBody Map<String,Object> body) {
        Usuario usuario = this.repoUser.findById(id);
        Figurita figuritaSubastada = this.repoFigurita.findById(body.get("figuritaId"));

        LocalDateTime fechaInicio =  LocalDateTime.now();
        LocalDateTime fechaFin = fechaInicio.plusMinutes((long) body.get("duracion"));

        Subasta nuevaSubasta = new Subasta(usuario, fechaInicio, fechaFin, figuritaSubastada, null);

        this.repoSubasta.save(nuevaSubasta);

        return ResponseEntity.ok(new TemporalDto("POST /subastas"));
    }

    @PostMapping("/{sub_id}/propuestas")
    public ResponseEntity<TemporalDto> ofertarEnSubasta(@PathVariable String sub_id, @RequestBody Map<String,Object> body) {


        return ResponseEntity.ok(new TemporalDto("POST /subastas/" + sub_id + "/propuestas"));
    }
}
