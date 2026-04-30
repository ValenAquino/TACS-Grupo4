package app.servicios;

import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
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

    List<Figurita> buscarFaltantes(String colId);
    List<FiguritaIntercambiable> buscarRepetidas(String colId, String tipo);
}
