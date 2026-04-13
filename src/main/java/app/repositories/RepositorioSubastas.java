package app.repositories;

import app.model.entities.Subasta;
import java.util.List;

public interface RepositorioSubastas {
    List<Subasta> findByUsuarioId(String userId);

    Subasta findById(String id);

    void save(Subasta subasta);
}