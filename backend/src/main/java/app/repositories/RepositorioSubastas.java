package app.repositories;

import app.model.entities.Subasta;
import java.util.List;

public interface RepositorioSubastas {
    List<Subasta> buscarPorAutorUserId(String userId);

    List<Subasta> buscarTodos();

    Subasta buscarPorId(String id);

    List<Subasta> buscarDondeParticipa(String userId);

    int contar();

    void guardar(Subasta subasta);
}