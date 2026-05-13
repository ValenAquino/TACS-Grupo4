package app.controllers;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.servicios.IServicioFigurita;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ControladorFigurita {
    private final IServicioFigurita figuritaService;

    @GetMapping("/figuritas")
    public ResponseEntity<PaginaResultado<FiguritaIntercambiableDto>> obtenerFiguritas(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer numero,
        @RequestParam(required = false) Seleccion seleccion,
        @RequestParam(required = false) String jugador,
        @RequestParam(required = false) MetodoIntercambio tipo,
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
