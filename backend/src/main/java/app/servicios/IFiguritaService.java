package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;

import java.util.List;

public interface IFiguritaService {

    /**
     * Busca figuritas aplicando filtros opcionales por número, selección y jugador.
     */
    List<Figurita> buscarFiguritas(FiguritasFiltro filtros);
}
