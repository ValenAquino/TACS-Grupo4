package app.controllers;

import app.dto.TemporalDto;
import app.model.entities.EstadoProceso;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    public ResponseEntity<TemporalDto> ofertarEnSubasta(@PathVariable String sub_id, @RequestHeader("id") String id, @RequestBody Map<String,Object> body) {
        Usuario usuarioOrigen = this.repoUser.findById(id);
        Usuario usuarioDestino = this.repoUser.findById(body.get("usuarioId"));
        Figurita figuritaBuscada = this.repoFigurita.findById(body.get("figuritaBuscadaId"));
        List<Figurita> figuritasOfrecidas = new ArrayList<>();

        Subasta subasta = this.repoSubasta.findById(sub_id);

        List<LinkedHashMap<String, Object>> rawFiguritasId = (ArrayList<LinkedHashMap<String, Object>>) body.get("figuritasOfrecidas");

        Boolean hayDuplicados = rawFiguritasId.size() != rawFiguritasId.stream().distinct().count();
        Boolean esLaFiguritaSubastada = figuritaBuscada.getId() == subasta.getFiguritaSubastada().getId();

        if (hayDuplicados || esLaFiguritaSubastada) {
            //El listado debe tener figuritas distintas
            return ResponseEntity.badRequest().body(new TemporalDto("POST /subastas/" + sub_id + "/propuestas"));
        }

        rawFiguritasId.forEach(figuritaId -> {
            Figurita figurita = this.repoFigurita.findById(figuritaId);
            figuritasOfrecidas.add(figurita);
        });

        Propuesta nuevaPropuesta = new Propuesta(usuarioOrigen, usuarioDestino, figuritasOfrecidas, figuritaBuscada, EstadoProceso.PENDIENTE);

        subasta.setPropuestaGanadora(nuevaPropuesta);

        this.repoSubasta.save(subasta);

        return ResponseEntity.ok(new TemporalDto("POST /subastas/" + sub_id + "/propuestas"));
    }
}
