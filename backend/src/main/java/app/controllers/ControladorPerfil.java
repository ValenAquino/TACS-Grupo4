package app.controllers;

import app.dto.ContadorDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.FiguritaDto;
import app.dto.NotificacionesDto;
import app.dto.OperacionesDto;
import app.dto.PerfilDto;
import app.dto.SugerenciaPaginadaDto;
import app.dto.filtros.SugerenciasFiltro;
import app.dto.request.CalificacionRequest;
import app.dto.request.PerfilRequest;
import app.servicios.IServicioPerfil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class ControladorPerfil {

    private final IServicioPerfil perfilService;

    @PostMapping("")
    public ResponseEntity<PerfilDto> crearPerfil(@RequestBody PerfilRequest body) {
        return ResponseEntity.ok(perfilService.crearPerfil(body));
    }

    //Todo: Eliminar en el futuro
    @GetMapping("/{user_id}/operaciones")
    public ResponseEntity<OperacionesDto> obtenerOperaciones(@PathVariable String user_id) {
        OperacionesDto operaciones = perfilService.obtenerOperacionesPerfil(user_id);
        if (operaciones == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(operaciones);
    }

    @GetMapping("/{user_id}/faltantes")
    public ResponseEntity<List<FiguritaDto>> obtenerFaltantes(@PathVariable String user_id) {
        return ResponseEntity.ok(perfilService.obtenerFaltantes(user_id));
    }

    @GetMapping("/{user_id}/repetidas")
    public ResponseEntity<List<FiguritaIntercambiableDto>> obtenerRepetidas(@PathVariable String user_id) {
        return ResponseEntity.ok(perfilService.obtenerRepetidas(user_id));
    }

    @PostMapping("/{perfil_id}/calificaciones")
    public ResponseEntity<Void> calificarPerfil(
        @PathVariable String perfil_id,
        @RequestBody CalificacionRequest body) {

        this.perfilService.agregarCalificacion(
            body.getUserId(),
            perfil_id,
            body.getValor(),
            body.getDescripcion(),
            body.getTransactionId(),
            body.getTipoTransaccion()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{user_id}/intercambiables")
    public ResponseEntity<List<FiguritaIntercambiableDto>> obtenerIntercambiables(
        @PathVariable String user_id) {

        return ResponseEntity.ok(
            perfilService.obtenerIntercambiablesPerfil(user_id)
        );
    }
    @GetMapping("/{user_id}/sugerencias")
    public ResponseEntity<SugerenciaPaginadaDto> obtenerSugerencias(@PathVariable String user_id, @ModelAttribute SugerenciasFiltro filtro) {

        SugerenciaPaginadaDto sugerenciasDto = this.perfilService.obtenerSugerencias(user_id, filtro);

        return ResponseEntity.ok().body(sugerenciasDto);
    }

    @GetMapping("/{user_id}/contadores")
    public ResponseEntity<List<ContadorDto>> obtenerContadores(@PathVariable String user_id) {
        return ResponseEntity.ok(this.perfilService.obtenerContadores(user_id));
    }

    @GetMapping("/{user_id}/notificaciones")
    public ResponseEntity<List<NotificacionesDto>> obtenerNotificaciones(@PathVariable String user_id) {
        return ResponseEntity.ok(this.perfilService.obtenerNotificaciones(user_id));
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<PerfilDto> obtenerPerfil(@PathVariable String user_id) {
        return ResponseEntity.ok(this.perfilService.obtenerPerfil(user_id));
    }
}