package app.servicios;

import app.model.entities.*;
import app.model.notificador.Mensaje;
import app.model.notificador.Notificacion;
import app.model.notificador.Notificador;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioNotificaciones;
import app.repositories.RepositorioUsuarios;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ColeccionService {

  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioUsuarios repositorioUsuarios;
  private final NotificacionService notificacionService;
  //private final Notificador notificador;

  public ColeccionService(RepositorioFiguritas repositorioFiguritas,
                          RepositorioColecciones repositorioColecciones,
                          RepositorioUsuarios repositorioUsuarios,
                          NotificacionService notificacionService
  ) {
    this.repositorioFiguritas = repositorioFiguritas;
    this.repositorioColecciones = repositorioColecciones;
    this.repositorioUsuarios = repositorioUsuarios;
    this.notificacionService = notificacionService;
  }

  public Figurita agregarFaltante(String colId, String figId) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    Figurita faltante = this.repositorioFiguritas.findById(figId);

    coleccion.agregarFaltante(faltante);

    repositorioColecciones.save(coleccion);

    return faltante;
  }

  public FiguritaIntercambiable agregarRepetida(String colId, String figId, Integer cantidadDisponible, List<String> modosIntercambio) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    Figurita figurita = this.repositorioFiguritas.findById(figId);

    FiguritaIntercambiable repetida = new FiguritaIntercambiable(
        figurita, cantidadDisponible, modosIntercambio.stream().map(MetodoIntercambio::fromString).toList());

    coleccion.agregarRepetida(repetida);
    repositorioColecciones.save(coleccion);

    List<Usuario> interesados = this.repositorioUsuarios.buscarPorFiguritaFaltante(repetida.getFigurita());

    String cuerpo = "Nueva figurita disponible, Numero: " + repetida.getFigurita().getId() + ", Cantidad: " + repetida.getCantidadDisponible();

    this.notificacionService.notificarInteresados(interesados, cuerpo);

    return repetida;
  }
}
