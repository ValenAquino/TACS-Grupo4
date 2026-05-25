package app.controllers;

import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.MetodoIntercambio;
import app.servicios.ServicioFigurita;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControladorFigurita {
    private final ServicioFigurita figuritaService;

    @GetMapping("/figuritas/intercambiables")
    public ResponseEntity<PaginaResultado<FiguritaIntercambiableDto>> obtenerFiguritas(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer numero,
        @RequestParam(required = false) String seleccion,
        @RequestParam(required = false) String jugador,
        @RequestParam(required = false) List<MetodoIntercambio> tipo,
        @RequestParam(defaultValue = "0") int pagina,
        @RequestParam(defaultValue = "12") int tamanioPagina
    ) {
        int tamanioDePaginaAcotado = Math.min(tamanioPagina, 40);
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(
                figuritaService.buscarPorQuery(q, tipo, pagina, tamanioDePaginaAcotado)
            );
        }
        return ResponseEntity.ok(
            figuritaService.buscarFiguritas(numero, seleccion, jugador, tipo, pagina, tamanioDePaginaAcotado)
        );
    }
}
