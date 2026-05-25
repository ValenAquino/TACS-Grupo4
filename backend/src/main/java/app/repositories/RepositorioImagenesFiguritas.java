package app.repositories;

import app.model.entities.ImagenFigurita;
import java.util.Optional;

public interface RepositorioImagenesFiguritas {

  void guardar(ImagenFigurita imagen);

  Optional<ImagenFigurita> buscarPorId(String id);

  /**
   * Intenta atomicamente iniciar el procesamiento de una figurita via findAndModify con upsert
   *
   * @return el documento previo, o {@code null} si no existía registro
   */
  ImagenFigurita iniciarProcesamiento(String figuritaId);

  /**
   * Intenta atomicamente retomar el procesamiento de un registro vencido (EN_PROCESO expirado)
   *
   * @return el documento previo si ganó, o {@code null} si otro proceso ya lo retomó
   */
  ImagenFigurita retomarProcesamiento(String figuritaId);
}
