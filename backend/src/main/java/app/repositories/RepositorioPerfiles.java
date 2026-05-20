package app.repositories;

import app.dto.filtros.SugerenciasFiltro;
import app.dto.paginacion.PaginaResultado;
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

    PaginaResultado<Sugerencia> generarSugerencias(Coleccion coleccion, SugerenciasFiltro filtros);
}
