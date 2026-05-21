package app.repositories;

import app.dto.paginacion.PaginaResultado;
import app.model.entities.Subasta;
import java.util.List;

public interface RepositorioSubastas {
    List<Subasta> buscarPorAutorUsuarioId(String userId);

    List<Subasta> buscarTodos();

    Subasta buscarPorId(String id);

    List<Subasta> buscarDondeParticipa(String userId);

    int contar();

    void guardar(Subasta subasta);

    PaginaResultado<Subasta> buscarPorAutor(String perfilId, Integer pagina, Integer limite);
}