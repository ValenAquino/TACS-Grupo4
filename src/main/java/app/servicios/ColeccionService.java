package app.servicios;

import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import org.springframework.stereotype.Service;

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

  public Figurita agregarFaltante(String colId, Long figId) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    if (coleccion == null) {
      //Agregar excepciones
      return null;
    }

    Figurita faltante = this.repositorioFiguritas.buscarPorId(figId);

    if(faltante == null) {
      //Agregar excepciones
      return null;
    }

    coleccion.agregarFaltante(faltante);

    repositorioColecciones.save(coleccion);

    return faltante;
  }
}
