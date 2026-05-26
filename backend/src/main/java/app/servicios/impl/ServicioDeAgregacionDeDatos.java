package app.servicios.impl;

import app.client.ImagenJugadorProveedor;
import app.model.entities.Figurita;
import app.repositories.RepositorioFiguritas;
import app.servicios.ServicioEnriquecimiento;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ServicioDeAgregacionDeDatos implements ServicioEnriquecimiento {

  private static final Logger log = LoggerFactory.getLogger(ServicioDeAgregacionDeDatos.class);
  private static final String STATUS_COMPLETADO = "COMPLETADO";
  private static final Duration TIMEOUT_PROCESAMIENTO = Duration.ofMinutes(10);

  private final ImagenJugadorProveedor imagenProveedor;
  private final RepositorioFiguritas repositorioFiguritas;

  public ServicioDeAgregacionDeDatos(ImagenJugadorProveedor imagenProveedor,
                                     RepositorioFiguritas repositorioFiguritas) {
    this.imagenProveedor = imagenProveedor;
    this.repositorioFiguritas = repositorioFiguritas;
  }

  @Override
  @Async
  public void enriquecer() {
    List<Figurita> pendientes = repositorioFiguritas.buscarPendientes(TIMEOUT_PROCESAMIENTO);
    for (Figurita figurita : pendientes) {
      try {
        procesarFigurita(figurita);
      } catch (Exception e) {
        log.error("Error inesperado al enriquecer '{}': {}", figurita.getJugador(), e.getMessage());
      }
    }
  }

  private void procesarFigurita(Figurita figurita) {
    Figurita reclamada = repositorioFiguritas.reclamarParaProcesamiento(figurita.getId(), TIMEOUT_PROCESAMIENTO);
    if (reclamada == null) {
      // otra instancia se adelantó
      return;
    }

    String url = imagenProveedor.buscarImagen(figurita.getJugador()).orElse(null);
    figurita.setImagenUrl(url);
    figurita.setImagenStatus(STATUS_COMPLETADO);
    figurita.setImagenCreadoEn(LocalDateTime.now());
    repositorioFiguritas.guardar(figurita);

    if (url != null) {
      log.info("Imagen obtenida: {}", figurita.getJugador());
    }
  }
}
