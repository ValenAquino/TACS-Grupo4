package app.servicios.impl;

import app.client.ImagenJugadorProveedor;
import app.model.entities.Figurita;
import app.model.entities.ImagenFigurita;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioImagenesFiguritas;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ServicioDeAgregacionDeDatos {

  private static final Logger log = LoggerFactory.getLogger(ServicioDeAgregacionDeDatos.class);
  private static final String STATUS_COMPLETADO = "COMPLETADO";

  private final RepositorioImagenesFiguritas repositorioImagenes;
  private final ImagenJugadorProveedor imagenProveedor;
  private final RepositorioFiguritas repositorioFiguritas;

  public ServicioDeAgregacionDeDatos(RepositorioImagenesFiguritas repositorioImagenes,
                                     ImagenJugadorProveedor imagenProveedor,
                                     RepositorioFiguritas repositorioFiguritas) {
    this.repositorioImagenes = repositorioImagenes;
    this.imagenProveedor = imagenProveedor;
    this.repositorioFiguritas = repositorioFiguritas;
  }

  @Async
  public void agregarDatos(List<Figurita> figuritas) {
    for (Figurita figurita : figuritas) {
      try {
        procesarFigurita(figurita);
      } catch (Exception e) {
        log.error("Error inesperado al enriquecer '{}': {}", figurita.getJugador(), e.getMessage());
      }
    }
  }

  private void procesarFigurita(Figurita figurita) {
    ImagenFigurita anterior = repositorioImagenes.iniciarProcesamiento(figurita.getId());

    // nadie proceso esta figurita todavía
    if (anterior == null) {
      enriquecerConApi(figurita);
      return;
    }

    // ya fue procesada
    if (anterior.getStatus().equals(STATUS_COMPLETADO)) {
      aplicarImagen(figurita, anterior.getImagenUrl());
      return;
    }

    // está siendo procesada o quedó inconsistente
    if (repositorioImagenes.retomarProcesamiento(figurita.getId()) != null) {
      enriquecerConApi(figurita);
    }
  }

  private void enriquecerConApi(Figurita figurita) {
    String imagenUrl = imagenProveedor.buscarImagen(figurita.getJugador()).orElse(null);
    ImagenFigurita imagen = new ImagenFigurita(figurita.getId(), imagenUrl, STATUS_COMPLETADO, LocalDateTime.now());

    repositorioImagenes.guardar(imagen);
    aplicarImagen(figurita, imagenUrl);

    if (imagenUrl != null) {
      log.info("Imagen obtenida: {}", figurita.getJugador());
    }
  }

  private void aplicarImagen(Figurita figurita, String imagenUrl) {
    figurita.setImagenUrl(imagenUrl);
    repositorioFiguritas.guardar(figurita);
  }
}
