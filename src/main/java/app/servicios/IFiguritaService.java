package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.model.entities.Seleccion;
import java.util.List;

public interface IFiguritaService {

    /**
     * Busca figuritas intercambiables aplicando filtros opcionales por número, selección y jugador.
     * Primero filtra en el repositorio de figuritas y luego cruza con las intercambiables disponibles.
     */
    List<FiguritaIntercambiableDto> buscarFiguritas(Integer numero, Seleccion seleccion,
                                                    String jugador);
}
