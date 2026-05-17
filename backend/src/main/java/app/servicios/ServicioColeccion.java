package app.servicios;

import app.dto.FaltantesDto;
import app.dto.Repetidas;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioColeccion {
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioPerfiles repositorioUsuarios;
  private final ServicioNotificacion notificacionService;

  public void agregarFaltante(String colId, String figId) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    Figurita faltante = this.repositorioFiguritas.buscarPorId(figId);

    coleccion.agregarFaltante(faltante);

    repositorioColecciones.guardar(coleccion);
  }

  public void agregarRepetida(String colId, String figId, Integer
      cantidadExistente, List<MetodoIntercambio> modosIntercambio) {

    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);
    Figurita figurita = this.repositorioFiguritas.buscarPorId(figId);

    FiguritaIntercambiable repetida = new FiguritaIntercambiable(
        figurita, cantidadExistente, modosIntercambio);

    coleccion.agregarRepetida(repetida);
    repositorioColecciones.guardar(coleccion);

    List<Perfil> interesados = this.repositorioUsuarios.buscarPorFiguritaFaltante(figurita);

    String cuerpo = "Nueva figurita disponible, Numero: " + figurita.getId() +
        ", Cantidad: " + cantidadExistente;

    this.notificacionService.notificarInteresados(interesados, cuerpo);
  }

  public FaltantesDto buscarFaltantes(String colId, FaltantesFiltro filtros) {
    return this.repositorioColecciones.buscarFaltantes(colId, filtros);

  }

  public Repetidas buscarRepetidas(String colId, RepetidasFiltro filtros) {
    return this.repositorioColecciones.buscarRepetidas(colId, filtros);
  }
}
