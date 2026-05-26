package app.repositories;

import app.dto.filtros.SugerenciasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.model.entities.Sugerencia;
import app.repositories.impl.campos.CamposPerfil;

import java.util.List;

public interface RepositorioPerfiles {

    void guardar(Perfil perfil);

    void guardar(Perfil perfil, CamposPerfil campos);

    Perfil buscarPorId(String id, CamposPerfil campos);

    Perfil buscarPorUsuarioId(String usuarioId, CamposPerfil campos);

    List<Perfil> buscarTodos(CamposPerfil campos);

    long contar();

    List<Perfil> buscarPorFiguritaFaltante(Figurita figurita, CamposPerfil campos);


    PaginaResultado<Sugerencia> generarSugerencias(Coleccion coleccion, SugerenciasFiltro filtros);
}
