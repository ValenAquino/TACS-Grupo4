package app.repositories;

import app.dto.paginacion.PaginaResultado;
import app.model.entities.Calificacion;
import app.model.entities.MetodoIntercambio;

public interface RepositorioCalificacion {
  void guardar(Calificacion calificacion);
  PaginaResultado<Calificacion> buscarPorDestinatario(String perfilId, Integer pagina, Integer limite);
  boolean yaCalifico(String perfilDestinoId, String perfilAutorId, String transaccionId, MetodoIntercambio tipoTransaccion);
}
