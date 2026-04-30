package app.servicios.impl;

import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.servicios.IColeccionService;
import app.servicios.INotificacionService;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColeccionService implements IColeccionService {
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioPerfiles repositorioUsuarios;
  private final INotificacionService notificacionService;

  @Override
  public void agregarFaltante(String colId, String figId) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);

    Figurita faltante = this.repositorioFiguritas.buscarPorId(figId);

    coleccion.agregarFaltante(faltante);

    repositorioColecciones.guardar(coleccion);
  }

  @Override
  public void agregarRepetida(String colId, String usuarioId, String figId, Integer
      cantidadExistente, List<String> modosIntercambio) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);
    Figurita figurita = this.repositorioFiguritas.buscarPorId(figId);

    Perfil perfil = this.repositorioUsuarios.buscarPorUsuarioId(usuarioId);

    FiguritaIntercambiable repetida = new FiguritaIntercambiable(
        figurita, cantidadExistente, modosIntercambio.stream()
        .map(MetodoIntercambio::fromString)
        .toList(), perfil.getId());

    coleccion.agregarRepetida(repetida);
    repositorioColecciones.guardar(coleccion);

    List<Perfil> interesados = this.repositorioUsuarios.buscarPorFiguritaFaltante(figurita);

    String cuerpo = "Nueva figurita disponible, Numero: " + figurita.getId() +
        ", Cantidad: " + cantidadExistente;

    this.notificacionService.notificarInteresados(interesados, cuerpo);
  }

  @Override
  public List<Figurita> buscarFaltantes(String colId) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);
    return coleccion.getFaltantes();
  }

  @Override
  public List<FiguritaIntercambiable> buscarRepetidas(String colId, String tipo) {
    Coleccion coleccion = this.repositorioColecciones.buscarPorId(colId);
    List<FiguritaIntercambiable> repetidas = coleccion.getRepetidas();

    if (Objects.equals(tipo, "subasta")) {
      repetidas = repetidas.stream()
          .filter(fig -> fig.getMetodos().contains(MetodoIntercambio.SUBASTA)
              || fig.getMetodos().contains(MetodoIntercambio.SUBASTA_E_INTERCAMBIO))
          .toList();
    }

    if (Objects.equals(tipo, "intercambio")) {
      repetidas = repetidas.stream()
          .filter(fig -> fig.getMetodos().contains(MetodoIntercambio.INTERCAMBIO)
              || fig.getMetodos().contains(MetodoIntercambio.SUBASTA_E_INTERCAMBIO))
          .toList();
    }

    return repetidas;
  }
}
