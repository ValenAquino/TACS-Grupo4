package app.repositories;

import app.dto.paginacion.PaginaResultado;
import app.model.entities.Calificacion;

public interface RepositorioCalificacion {
  void guardar(Calificacion calificacion);

  PaginaResultado<Calificacion> buscarPorDestinatario(String perfilId, Integer pagina, Integer limite);
}
