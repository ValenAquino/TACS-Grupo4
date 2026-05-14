package app.servicios.impl;

import app.dto.ContadorDto;
import app.dto.FiguritaDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.NotificacionesDto;
import app.dto.OperacionesDto;
import app.dto.PerfilDto;
import app.dto.SugerenciaDto;
import app.dto.SugerenciaPaginadaDto;
import app.dto.calificaciones.CalificacionesDto;
import app.dto.filtros.SugerenciasFiltro;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.model.entities.Calificacion;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import app.model.entities.Sugerencia;
import app.model.entities.Perfil;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioNotificaciones;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioPerfiles;
import app.servicios.IPerfilService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerfilService implements IPerfilService {

  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioPropuestas repositorioPropuestas;
  private final RepositorioSubastas repositorioSubastas;
  private final RepositorioFiguritasIntercambiables repositorioFiguritasIntercambiables;
  private final RepositorioNotificaciones repositorioNotificaciones;
//TODO ya no es necesario este metodo, eliminar


  @Override
  public List<FiguritaDto> obtenerFaltantes(String userId) {
    Perfil perfil = repositorioPerfiles.buscarPorUsuarioId(userId);
    return perfil.getColeccion().getFaltantes().stream()
        .map(FiguritaDto::new)
        .toList();
  }
  //TODO que se filtren las que cantidadExistentes == 0
  @Override
  public List<FiguritaIntercambiableDto> obtenerRepetidas(String userId) {
    Perfil perfil = repositorioPerfiles.buscarPorUsuarioId(userId);
    return perfil.getColeccion().getRepetidas().stream()
        .map(FiguritaIntercambiableDto::new)
        .toList();
  }

  @Override
  public List<FiguritaIntercambiableDto> obtenerIntercambiablesPerfil(String userId) {
      Perfil perfil = repositorioPerfiles.buscarPorId(userId);
      if (perfil == null) throw new NotFoundException("Perfil no encontrado");

      return repositorioFiguritasIntercambiables.buscarPorUsuarioId(userId)
          .stream()
          .map(FiguritaIntercambiableDto::new)
          .toList();
  }

  @Override
  public void agregarCalificacion(String userAutorId, String perfilDestinoId, Integer valor, String descripcion, String transactionId, MetodoIntercambio tipoTransaccion) {
    if (valor == null) {
      throw new BadRequestException("El valor de la calificación no puede ser nulo");
    }
    if (valor < 1 || valor > 5) {
      throw new BadRequestException("El valor de la calificación debe estar entre 1 y 5");
    }

    Perfil perfilDestino = this.repositorioPerfiles.buscarPorId(perfilDestinoId);
    if (perfilDestino == null) throw new NotFoundException("Perfil no encontrado: " + perfilDestinoId);

    Perfil autor = this.repositorioPerfiles.buscarPorUsuarioId(userAutorId);
    if (autor == null) throw new NotFoundException("Perfil no encontrado: " + userAutorId);

    if (tipoTransaccion.equals(MetodoIntercambio.INTERCAMBIO)){
      Propuesta propuesta = this.repositorioPropuestas.buscarPorId(transactionId);
      propuesta.marcarCalificada();
    }

    boolean yaCalifico = perfilDestino.getCalificaciones().stream()
        .anyMatch(c -> autor.getId().equals(c.getAutor().getId())
            && Objects.equals(transactionId, c.getTransactionId())
            && c.getTipoTransaccion() == tipoTransaccion);

    if (yaCalifico) throw new BadRequestException("Ya calificaste esta transacción");

    Calificacion calificacion = new Calificacion(
        UUID.randomUUID().toString(),
        autor,
        valor,
        descripcion,
        transactionId,
        tipoTransaccion
    );
    System.out.println(perfilDestino.obtenerCalificacionMedia());

    perfilDestino.agregarNuevaCalificacion(calificacion);

    System.out.println(perfilDestino.obtenerCalificacionMedia());
    this.repositorioPerfiles.guardar(perfilDestino);
  }

  @Override
  public SugerenciaPaginadaDto obtenerSugerencias(String userId, SugerenciasFiltro filtros) {
    Perfil perfilObjetivo = this.repositorioPerfiles.buscarPorUsuarioId(userId);
    List<Perfil> perfiles = this.repositorioPerfiles.buscarTodos();
    List<Sugerencia> sugerencias = new ArrayList<>();

    //TODO: Esto debe estar en el motor de la base de datos en el futuro
    perfiles.forEach(perfil -> {
      Sugerencia sugerencia = new Sugerencia(perfil, new ArrayList<>(), new ArrayList<>());

      perfil.getColeccion().getRepetidas().forEach(miRepetida -> {

        perfilObjetivo.getColeccion().getRepetidas().forEach(suRepetida -> {

          boolean yoNecesito = perfilObjetivo
              .getColeccion()
              .getFaltantes()
              .contains(miRepetida.getFigurita());

          boolean elNecesita = perfil
              .getColeccion()
              .getFaltantes()
              .contains(suRepetida.getFigurita());

          if (yoNecesito && elNecesita) {
            sugerencia.getFiguritasSugeridas().add(miRepetida.getFigurita());
            sugerencia.getFiguritasNecesarias().add(suRepetida.getFigurita());
          }
        });
      });
      if (!sugerencia.getFiguritasSugeridas().isEmpty() && !sugerencia.getFiguritasNecesarias().isEmpty()) {
        sugerencias.add(sugerencia);
      }
    });


    List<Sugerencia> sugerenciasFiltradas = sugerencias.stream().filter(filtros::verifica).toList();

    int resultados = sugerenciasFiltradas.size();
    int paginaActual = filtros.paginaActual();
    int paginasTotales = (resultados + filtros.limite() - 1) / filtros.limite();

    List<SugerenciaDto> sugerenciasDto = sugerenciasFiltradas.stream().map(SugerenciaDto::new).toList();

    return new SugerenciaPaginadaDto(sugerenciasDto, resultados, paginaActual, paginasTotales);
  }

  @Override
  public List<ContadorDto> obtenerContadores(String userId) {
    Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId);

    List<ContadorDto> contadores = new ArrayList<>();

    contadores.add(new ContadorDto("repetidas",perfil.getColeccion().getRepetidas().size()));
    contadores.add(new ContadorDto("faltantes",perfil.getColeccion().getFaltantes().size()));

    return contadores;
  }

  @Override
  public List<NotificacionesDto> obtenerNotificaciones(String userId) {
      Perfil perfil = repositorioPerfiles.buscarPorId(userId);

    return this.repositorioNotificaciones.buscarPorUsuario(perfil).stream().map(NotificacionesDto::new).toList();
  }

  @Override
  public CalificacionesDto obtenerCalificaciones(String perfilId, Integer pagina, Integer limite) {
    return this.repositorioPerfiles.buscarCalificaciones(perfilId, pagina, limite);
  }

  @Override
  public PerfilDto obtenerPerfil(String userId) {
    Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId);
    if (perfil == null) throw new NotFoundException("Perfil no encontrado para el usuario: " + userId);
    return new PerfilDto(perfil);
  }
}
