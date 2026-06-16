package app.servicios;

import app.dto.FiguritaDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.dto.paginacion.Repetidas;
import app.dto.request.EditarRepetidaRequest;
import app.exceptions.BadRequestException;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.dto.filtros.FaltantesFiltro;
import app.dto.filtros.RepetidasFiltro;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;

import java.util.HashSet;
import java.util.List;

import app.repositories.impl.campos.CamposPerfil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicioColeccion {
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioPerfiles repositorioPerfiles;
  private final ServicioNotificacion notificacionService;

  public void agregarFaltante(String colId, String figId) {
    Figurita faltante = this.repositorioFiguritas.buscarPorId(figId);

    repositorioColecciones.agregarFaltante(colId, faltante);
  }

  @Transactional
  public void agregarRepetida(String colId, String perfilId, String figId, Integer
      cantidadExistente, List<MetodoIntercambio> modosIntercambio) {

    Figurita figurita = this.repositorioFiguritas.buscarPorId(figId);

    FiguritaIntercambiable repetida = new FiguritaIntercambiable(
        figurita, cantidadExistente, modosIntercambio, perfilId);

    this.repositorioColecciones.agregarRepetida(colId, repetida);

    List<Perfil> interesados = this.repositorioPerfiles.buscarPorFiguritaFaltante(figurita, new CamposPerfil(true));

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
    String colIdFaltantes = resolverColIdFaltantes(filtros.perfilId());

    Repetidas<FiguritaIntercambiable> repetidas =
        this.repositorioColecciones.buscarRepetidas(colId, filtros, colIdFaltantes);

    PaginaResultado<FiguritaIntercambiableDto> paginacionDto =
        repetidas.getData().mapearA(FiguritaIntercambiableDto::new);

    return new Repetidas<>(repetidas.getPublicadas(), repetidas.getDisponibles(), paginacionDto);
  }

  @Transactional
  public void editarRepetida(String colId, String figId, EditarRepetidaRequest req) {
    FiguritaIntercambiable repetidaAmodificar = this.repositorioColecciones.buscarRepetida(colId, figId);

    if(req.cantidadNueva() < repetidaAmodificar.getCantidadReservada()) {
      throw new BadRequestException("No se puede tener menos cantidad que las reservadas");
    }

    repetidaAmodificar.setCantidadExistente(req.cantidadNueva());

    if (!req.metodos().isEmpty()) {
      validarQueNoQuiteMetodos(
          repetidaAmodificar.getMetodos(),
          req.metodos()
      );

      repetidaAmodificar.setMetodos(req.metodos());
    }

    this.repositorioColecciones.actualizarRepetida(colId, figId, repetidaAmodificar);
  }

  private String resolverColIdFaltantes(String perfilId) {
    if (perfilId == null) return null;
    Perfil perfilFaltantes = this.repositorioPerfiles.buscarPorId(perfilId, new CamposPerfil(false));
    return perfilFaltantes.getColeccion().getId();
  }

  private void validarQueNoQuiteMetodos(
      List<MetodoIntercambio> actuales,
      List<MetodoIntercambio> nuevos
  ) {
    if (!new HashSet<>(nuevos).containsAll(actuales)) {
      throw new BadRequestException(
          "No se pueden eliminar métodos de intercambio existentes"
      );
    }
  }
}
