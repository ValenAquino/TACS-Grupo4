package app.repositories;

import app.model.entities.Figurita;
import app.model.entities.Perfil;
import java.util.List;

public interface RepositorioPerfiles {

    Perfil buscarPorId(String id);

    List<Perfil> buscarTodos();

    int contar();

    List<Perfil> buscarPorFiguritaFaltante(Figurita figurita);

    void guardar(Perfil perfil);
}
