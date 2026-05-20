package app.servicios;

import app.dto.FiguritaDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.dto.filtros.FaltantesFiltro;
import app.dto.filtros.RepetidasFiltro;
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

  public PaginaResultado<FiguritaDto> buscarFaltantes(String colId, FaltantesFiltro filtros) {
    PaginaResultado<Figurita> resultado = this.repositorioColecciones.buscarFaltantes(colId, filtros);

    return new PaginaResultado<>(
        resultado.contenido().stream().map(FiguritaDto::new).toList(),
        resultado.cantidadDeElementos(),
        resultado.cantidadDePaginas(),
        resultado.numero());
  }

  public Repetidas<FiguritaIntercambiableDto> buscarRepetidas(String colId, RepetidasFiltro filtros) {
    Repetidas<FiguritaIntercambiable> repetidas = this.repositorioColecciones.buscarRepetidas(colId, filtros);
    PaginaResultado<FiguritaIntercambiableDto> paginacionDto = repetidas.getData().mapearA(FiguritaIntercambiableDto::new);

    return new Repetidas<>(repetidas.getPublicadas(), repetidas.getDisponibles(), paginacionDto);
  }
}
