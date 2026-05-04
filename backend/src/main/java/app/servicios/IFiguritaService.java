package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;

public interface IFiguritaService {

    /**
     * Busca figuritas intercambiables aplicando filtros opcionales y paginación.
     *
     * <p>Filtra primero en {@code RepositorioFiguritas} por {@code numero}, {@code seleccion}
     * y {@code jugador} (contains, case-insensitive), luego cruza con las intercambiables
     * y aplica el filtro {@code tipo}: {@code INTERCAMBIO} y {@code SUBASTA} también incluyen
     * figuritas con {@code SUBASTA_E_INTERCAMBIO}. Sin resultados retorna página vacía.
     *
     * @param numero    número exacto de figurita, o {@code null} para ignorar el filtro
     * @param seleccion selección nacional, o {@code null} para ignorar el filtro
     * @param jugador   fragmento del nombre del jugador, o {@code null} para ignorar el filtro
     * @param tipo      tipo de intercambio deseado, o {@code null} para devolver todos
     * @param pagina    número de página 0-indexed
     * @param tamanioPagina elementos por página; el controller acota a 40 antes de llamar aquí
     */
    PaginaResultado<FiguritaIntercambiableDto> buscarFiguritas(
        Integer numero, Seleccion seleccion, String jugador, MetodoIntercambio tipo,
        int pagina, int tamanioPagina);
}
