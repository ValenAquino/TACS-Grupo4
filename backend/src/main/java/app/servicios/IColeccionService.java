package app.servicios;

import app.dto.RepetidasDto;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
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
    void agregarRepetida(String colId, String userId, String figId, Integer
        cantidadDisponible, List<String> modosIntercambio);

    /**
     * Busca las figuritas faltantes de la coleccion con la opcion de aplicar filtros.
     */
    List<Figurita> buscarFaltantes(String colId);

    /**
     * Busca las figuritas faltantes de la coleccion con la opcion de aplicar filtros.
     */
    RepetidasDto buscarRepetidas(String colId, RepetidasFiltro filtros);
}
