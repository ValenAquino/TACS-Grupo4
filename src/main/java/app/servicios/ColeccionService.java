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
  private final RepositorioNotificaciones repositorioNotificaciones;
  //private final Notificador notificador;

  public ColeccionService(RepositorioFiguritas repositorioFiguritas,
                          RepositorioColecciones repositorioColecciones,
                          RepositorioUsuarios repositorioUsuarios,
                          RepositorioNotificaciones repositorioNotificaciones

  ) {
    this.repositorioFiguritas = repositorioFiguritas;
    this.repositorioColecciones = repositorioColecciones;
    this.repositorioUsuarios = repositorioUsuarios;
    this.repositorioNotificaciones = repositorioNotificaciones;
    //this.notificador = notificador;
  }

  public Figurita agregarFaltante(String colId, String figId) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    Figurita faltante = this.repositorioFiguritas.buscarPorId(figId);

    coleccion.agregarFaltante(faltante);

    repositorioColecciones.save(coleccion);

    return faltante;
  }

  public FiguritaIntercambiable agregarRepetida(String colId, String figId, Integer cantidadDisponible, List<String> modosIntercambio) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    Figurita figurita = this.repositorioFiguritas.buscarPorId(figId);

    FiguritaIntercambiable repetida = new FiguritaIntercambiable(
        figurita, cantidadDisponible, modosIntercambio.stream().map(MetodoIntercambio::fromString).toList());

    coleccion.agregarRepetida(repetida);
    repositorioColecciones.save(coleccion);

    List<Usuario> interesados = this.repositorioUsuarios.buscarPorFiguritaFaltante(figurita);

    this.notificarInteresados(interesados, repetida);

    return repetida;
  }

  private void notificarInteresados(List<Usuario> interesados, FiguritaIntercambiable repetida) {
    interesados.forEach(u -> {

      String cuerpo = String.format(
          "Nueva figurita disponible!%nNumero: %d%nCantidad: %d",
          repetida.getFigurita().getId(),
          repetida.getCantidadDisponible()
      );

      Mensaje mensaje = new Mensaje(cuerpo, LocalDateTime.now());
      //this.notificador.enviarNotificacion(mensaje, u);
      this.repositorioNotificaciones.save(new Notificacion(mensaje, u));
    });
  }
}
