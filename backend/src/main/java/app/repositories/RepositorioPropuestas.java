package app.repositories;

import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.propuesta.PropuestasDto;
import app.model.entities.Propuesta;
import java.util.List;

public interface RepositorioPropuestas {
    List<Propuesta> buscarTodos();
    Propuesta buscarPorId(String id);
    int contar();
    void guardar(Propuesta propuesta);
    public PaginaResultado<Propuesta> buscarPorAutorId(String perfilId, PropuestasFiltro filtros);
    public PaginaResultado<Propuesta> buscarPorDestinatarioId(String perfilId, PropuestasFiltro filtros);
}