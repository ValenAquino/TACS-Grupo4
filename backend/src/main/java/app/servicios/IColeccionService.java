package app.servicios;

import app.dto.FaltantesDto;
import app.dto.RepetidasDto;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;

import java.util.List;

public interface IColeccionService {

    /**
     * Agrega la figurita indicada a la lista de faltantes de la colección.
     * Propaga {@link app.exceptions.FiguritaDuplicadaException} si ya está registrada como faltante.
     */
    void agregarFaltante(String colId, String figId);

    /**
     * Agrega una figurita repetida a la colección y notifica a los usuarios
     * que la tienen en su lista de faltantes.
     */
    void agregarRepetida(String colId, String figId, Integer
        cantidadDisponible, List<MetodoIntercambio> modosIntercambio);

    /**
     * Busca las figuritas faltantes de la coleccion con la opcion de aplicar filtros.
     */
    FaltantesDto buscarFaltantes(String colId, FaltantesFiltro filtros);

    /**
     * Busca las figuritas faltantes de la coleccion con la opcion de aplicar filtros.
     */
    RepetidasDto buscarRepetidas(String colId, RepetidasFiltro filtros);
}
