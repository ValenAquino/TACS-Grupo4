package app.repositories;

import app.dto.filtros.PropuestasFiltro;
import app.dto.propuesta.PropuestasDto;
import app.model.entities.Propuesta;
import java.util.List;

public interface RepositorioPropuestas {
    // Propuestas enviadas
    PropuestasDto buscarPorAutorId(String userId, PropuestasFiltro filtros);
    // Propuestas recibidas
    PropuestasDto buscarPorDestinatarioId(String userId, PropuestasFiltro filtros);
    List<Propuesta> buscarTodos();
    Propuesta buscarPorId(String id);
    int contar();
    void guardar(Propuesta propuesta);
}