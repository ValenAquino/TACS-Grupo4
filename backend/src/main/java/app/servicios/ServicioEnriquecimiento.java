package app.servicios;

public interface ServicioEnriquecimiento {

  /**
   * Busca imágenes de jugadores para todas las figuritas pendientes y las persiste de forma
   * asíncrona. Es idempotente: ignora las figuritas ya procesadas y reintenta las que quedaron
   * colgadas.
   */
  void enriquecer();
}
