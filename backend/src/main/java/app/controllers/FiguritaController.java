package app.controllers;

import app.dto.FiguritaIntercambiableDto;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
import app.servicios.IFiguritaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FiguritaController {
    private final IFiguritaService figuritaService;

    @GetMapping("/figuritas")
    public ResponseEntity<List<Figurita>> obtenerFiguritas(
        @ModelAttribute FiguritasFiltro filtros
    ) {
        return ResponseEntity.ok(figuritaService.buscarFiguritas(filtros));
    }
}
