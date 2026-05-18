package app.repositories;

import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.model.entities.Sugerencia;

import java.util.List;

public interface RepositorioPerfiles {

    Perfil buscarPorId(String id);

    Perfil buscarPorUsuarioId(String usuarioId);

    List<Perfil> buscarTodos();

    long contar();

    List<Perfil> buscarPorFiguritaFaltante(Figurita figurita);

    void guardar(Perfil perfil);

    List<Sugerencia> generarSugerencias(Coleccion coleccion, String perfilId);
}
