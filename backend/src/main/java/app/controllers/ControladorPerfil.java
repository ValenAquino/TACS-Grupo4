package app.controllers;

import app.dto.ContadorDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.FiguritaDto;
import app.dto.NotificacionesDto;
import app.dto.PerfilDto;
import app.dto.SugerenciaPaginadaDto;
import app.dto.calificaciones.CalificacionesDto;
import app.dto.filtros.SugerenciasFiltro;
import app.dto.request.CalificacionRequest;
import java.util.List;

import app.servicios.ServicioPerfil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class ControladorPerfil {

    private final ServicioPerfil perfilService;

    //TODO: Eliminar esta funcion. Si se quieren las repetidas de un perfil, se tiene que usar la coleccion.
    @GetMapping("/{user_id}/faltantes")
    public ResponseEntity<List<FiguritaDto>> obtenerFaltantes(@PathVariable String user_id) {
        return ResponseEntity.ok(perfilService.obtenerFaltantes(user_id));
    }

    //TODO: Eliminar esta funcion. Si se quieren las repetidas de un perfil, se tiene que usar la coleccion.
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

    @GetMapping("/{perfil_id}/calificaciones")
    public CalificacionesDto obtenerCalificaciones(
        @PathVariable String perfil_id,
        @RequestParam Integer pagina,
        @RequestParam Integer limite

    ) {
        return this.perfilService.obtenerCalificaciones(perfil_id, pagina, limite);
    }

    //TODO: Eliminar esta funcion. Si se quieren las repetidas de un perfil, se tiene que usar la coleccion.
    @GetMapping("/{user_id}/intercambiables")
    public ResponseEntity<List<FiguritaIntercambiableDto>> obtenerIntercambiables(
        @PathVariable String user_id) {

        return ResponseEntity.ok(
            perfilService.obtenerIntercambiablesPerfil(user_id)
        );
    }


    @GetMapping("/{perfil_id}/sugerencias")
    public ResponseEntity<SugerenciaPaginadaDto> obtenerSugerencias(@PathVariable String perfil_id, @ModelAttribute SugerenciasFiltro filtro) {

        SugerenciaPaginadaDto sugerenciasDto = this.perfilService.obtenerSugerencias(perfil_id, filtro);

        return ResponseEntity.ok().body(sugerenciasDto);
    }

    @GetMapping("/{perfil_id}/contadores")
    public ResponseEntity<List<ContadorDto>> obtenerContadores(@PathVariable String perfil_id) {
        return ResponseEntity.ok(this.perfilService.obtenerContadores(perfil_id));
    }

    @GetMapping("/{perfil_id}/notificaciones")
    public ResponseEntity<List<NotificacionesDto>> obtenerNotificaciones(@PathVariable String perfil_id) {
        return ResponseEntity.ok(this.perfilService.obtenerNotificaciones(perfil_id));
    }

    @GetMapping("/{perfil_id}")
    public ResponseEntity<PerfilDto> obtenerPerfil(@PathVariable String perfil_id) {
        return ResponseEntity.ok(this.perfilService.obtenerPerfil(perfil_id));
    }
}