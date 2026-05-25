package app.client;

import java.util.Optional;

public interface ImagenJugadorProveedor {

  /**
   * Busca la URL de imagen del jugador en el proveedor externo.
   *
   * @param nombreJugador nombre del jugador (puede contener tildes y caracteres especiales)
   * @return la URL de imagen, o {@link Optional#empty()} si no se encuentra o hay error
   */
  Optional<String> buscarImagen(String nombreJugador);
}
