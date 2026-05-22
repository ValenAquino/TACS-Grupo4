package app.repositories;

import app.dto.filtros.SubastasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.Subasta;
import java.util.List;

public interface RepositorioSubastas {

    PaginaResultado<Subasta> buscarTodos(SubastasFiltro filtros);

    Subasta buscarPorId(String id);

    List<Subasta> buscarDondeParticipa(String userId);

    int contar();

    void guardar(Subasta subasta);

    PaginaResultado<Subasta> buscarPorAutor(String perfilId, Integer pagina, Integer limite);
}