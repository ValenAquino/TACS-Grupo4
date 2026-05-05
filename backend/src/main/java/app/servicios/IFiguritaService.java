package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;

public interface IFiguritaService {

    /**
     * Busca figuritas intercambiables aplicando filtros opcionales y paginación.
     * Resultados ordenados por número de figurita (ascendente).
     *
     * <p>Delega el filtrado, ordenamiento y paginación al repositorio de intercambiables,
     * y enriquece cada resultado con nombre y reputación del ofertante.
     *
     * @param numero        número exacto de figurita, o {@code null} para ignorar
     * @param seleccion     selección nacional, o {@code null} para ignorar
     * @param jugador       fragmento del nombre (contains, case-insensitive), o {@code null}
     * @param tipo          tipo de intercambio deseado, o {@code null} para devolver todos
     * @param pagina        número de página 0-indexed
     * @param tamanioPagina elementos por página; el controller acota a 40
     */
    PaginaResultado<FiguritaIntercambiableDto> buscarFiguritas(
        Integer numero, Seleccion seleccion, String jugador, MetodoIntercambio tipo,
        int pagina, int tamanioPagina);
}
