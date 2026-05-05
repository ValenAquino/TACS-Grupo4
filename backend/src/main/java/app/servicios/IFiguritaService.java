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

    /**
     * Búsqueda por texto libre con OR entre campos y AND entre términos.
     *
     * @param q             texto libre; los términos separados por espacio se combinan con AND,
     *                      y cada término se busca en jugador, selección y número
     * @param tipo          filtro de tipo de intercambio, o {@code null} para todos
     * @param pagina        número de página 0-indexed
     * @param tamanioPagina elementos por página; el controller acota a 40
     */
    PaginaResultado<FiguritaIntercambiableDto> buscarPorQuery(
        String q, MetodoIntercambio tipo, int pagina, int tamanioPagina);
}
