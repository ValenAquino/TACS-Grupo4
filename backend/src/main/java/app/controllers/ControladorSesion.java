package app.controllers;

import app.dto.EstadisticasDto;
import app.dto.PerfilDto;
import app.dto.request.UsuarioRequest;
import app.servicios.ServicioEstadisticas;
import app.servicios.ServicioSesion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControladorSesion {

    private final ServicioEstadisticas estadisticasService;

    private final ServicioSesion sesionService;

    @PostMapping("/usuarios")
    public ResponseEntity<PerfilDto> crearPerfil(@RequestBody UsuarioRequest body) {
        this.sesionService.crearUsuario(body);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/administrador/estadisticas")
    public ResponseEntity<EstadisticasDto> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
    }
}
