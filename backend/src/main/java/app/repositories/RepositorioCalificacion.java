package app.repositories;

import app.dto.paginacion.PaginaResultado;
import app.model.entities.Calificacion;

public interface RepositorioCalificacion {
  void guardar(Calificacion calificacion);

  PaginaResultado<Calificacion> buscarPorPerfil(String perfilId, Integer pagina, Integer limite);
}
