package app.servicios;

import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.impl.RepositorioColeccionesEnMemoria;
import app.repositories.impl.RepositorioFiguritasEnMemoria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColeccionService {

  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioColecciones repositorioColecciones;

  public ColeccionService(RepositorioFiguritas repositorioFiguritas,
                          RepositorioColecciones repositorioColecciones
  ) {
    this.repositorioFiguritas = repositorioFiguritas;
    this.repositorioColecciones = repositorioColecciones;
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

    //Enviar notificaciones

    return repetida;
  }
}
