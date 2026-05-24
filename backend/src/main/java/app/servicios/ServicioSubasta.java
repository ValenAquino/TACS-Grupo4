package app.servicios;

import app.dto.filtros.SubastasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.EditarOfertaRequest;
import app.dto.subasta.MiSubastaActivaDto;
import app.dto.subasta.MiSubastaFinalizadaDto;
import app.dto.subasta.SubastaDto;
import app.dto.subasta.SubastaParticipoDto;
import app.exceptions.BadRequestException;
import app.exceptions.NotFoundException;
import app.model.entities.*;
import app.repositories.RepositorioCalificacion;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioSubastas;
import app.repositories.impl.campos.CamposColeccion;
import java.time.LocalDateTime;
import java.util.List;

import app.repositories.impl.campos.CamposPerfil;
import app.repositories.impl.campos.CamposSubasta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicioSubasta {
  private final RepositorioSubastas repoSubasta;
  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioFiguritas repoFigurita;
  private final RepositorioCalificacion repoCalificacion;
  private final RepositorioColecciones repositorioColecciones;
  private final ServicioNotificacion notificacionService;

  @Transactional
  public void crearSubasta(String perfilId, String figuritaId, Integer duracionEnHoras,
                           List<String> figuritasDeseadasIds, Integer calificacionMinima) {
    CamposPerfil conColeccion = new CamposPerfil(true);
    Perfil perfil = this.repositorioPerfiles.buscarPorId(perfilId, conColeccion);
    Figurita figuritaSubastada = this.repoFigurita.buscarPorId(figuritaId);

    List<Figurita> figuritasDeseadas = figuritasDeseadasIds.stream()
        .map(this.repoFigurita::buscarPorId)
        .toList();

    LocalDateTime fechaInicio = LocalDateTime.now();
    LocalDateTime fechaFin = fechaInicio.plusHours(duracionEnHoras.longValue());

    Subasta nuevaSubasta = Subasta.builder()
        .autor(perfil).figuritaSubastada(figuritaSubastada)
        .fechaInicio(fechaInicio).fechaCierre(fechaFin)
        .figuritasSolicitadas(figuritasDeseadas)
        .calificacionMinimaSolicitada(calificacionMinima)
        .build();

    nuevaSubasta.reservarFiguritaSubastada();

    this.repoSubasta.guardar(nuevaSubasta);
    this.repositorioColecciones.guardar(perfil.getColeccion(), new CamposColeccion(true, false));

    CamposPerfil conMedio = new CamposPerfil(true);
    List<Perfil> interesados = this.repositorioPerfiles
        .buscarPorFiguritaFaltante(figuritaSubastada, conMedio);

    this.notificacionService.notificarInteresados(
        interesados, "Encontramos una subasta de una figurita que te falta!");
  }

  @Transactional
  public void ofertarEnSubasta(String autorId, String subastaId, List<String> rawFiguritasId) {
    CamposPerfil conColeccion = new CamposPerfil(true);
    Perfil autor = this.repositorioPerfiles.buscarPorId(autorId, conColeccion);
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);
    Perfil destinatario = subasta.getAutor();

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    if (rawFiguritasId.size() != rawFiguritasId.stream().distinct().count()) {
      throw new BadRequestException("Figuritas ofrecidas repetidas");
    }

    List<Figurita> figuritasOfrecidas = rawFiguritasId.stream()
        .map(this.repoFigurita::buscarPorId)
        .toList();

    Propuesta nuevaPropuesta = Propuesta.builder()
        .autor(autor)
        .destinatario(destinatario)
        .figuritaBuscada(subasta.getFiguritaSubastada())
        .figuritasOfrecidas(figuritasOfrecidas)
        .build();

    subasta.agregarOferta(nuevaPropuesta);

    this.repositorioColecciones.guardar(autor.getColeccion(), new CamposColeccion(true, false));
    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void editarOfertaEnSubasta(String perfilId, String subastaId, String ofertaId, EditarOfertaRequest body){
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);
    List<Figurita> nuevasFiguritas = this.repoFigurita.buscarPorIds(body.getFiguritasOfrecidasId());

    Propuesta oferta = subasta.modificarFiguritasDeOferta(ofertaId, perfilId, nuevasFiguritas);

    CamposColeccion camposColeccion = new CamposColeccion(true, false);
    this.repositorioColecciones.guardar(oferta.getAutor().getColeccion(), camposColeccion);
    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void cancelarOferta(String perfilId, String subastaId, String ofertaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    Propuesta oferta = subasta.cancelarOferta(ofertaId, perfilId);

    CamposColeccion camposColeccion = new CamposColeccion(true, false);
    this.repositorioColecciones.guardar(oferta.getAutor().getColeccion(), camposColeccion);

    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void seleccionarOferta( String perfilId, String subastaId, String ofertaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    subasta.seleccionarOferta(ofertaId, perfilId);

    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void rechazarOferta(String perfilId, String subastaId, String ofertaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    Propuesta oferta = subasta.rechazarOferta(ofertaId, perfilId);

    CamposColeccion camposColeccion = new CamposColeccion(true, false);
    this.repositorioColecciones.guardar(oferta.getAutor().getColeccion(), camposColeccion);

    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void cancelarSubasta(String perfilId, String subastaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);
    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }
    subasta.cancelar(perfilId);

    CamposColeccion camposColeccion = new CamposColeccion(true, false);
    subasta.getOfertas().stream()
        .filter(o -> o.getEstadoActual().getValor() != EstadoProceso.CANCELADO)
        .map(o -> o.getAutor().getColeccion())
        .distinct()
        .forEach(col -> this.repositorioColecciones.guardar(col, camposColeccion));

    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void cerrarSubasta(String perfilId, String subastaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);
    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    Propuesta seleccionada = subasta.obtenerSeleccionada();

    subasta.cerrar(perfilId);

    CamposColeccion soloRepetidas = new CamposColeccion(true, false);
    CamposColeccion todo = new CamposColeccion(true, true);

    // rechazados: solo repetidas
    subasta.getOfertas().stream()
        .filter(o -> seleccionada == null || !o.getId().equals(seleccionada.getId()))
        .filter(o -> o.getEstadoActual().getValor() != EstadoProceso.CANCELADO)
        .map(o -> o.getAutor().getColeccion())
        .distinct()
        .forEach(col -> this.repositorioColecciones.guardar(col, soloRepetidas));

    // ganador y autor: repetidas y faltantes
    if (seleccionada != null) {
      this.repositorioColecciones.guardar(seleccionada.getAutor().getColeccion(), todo);
      this.repositorioColecciones.guardar(subasta.getAutor().getColeccion(), todo);
    }

    this.repoSubasta.guardar(subasta, camposSubasta);
  }

    public PaginaResultado<?> obtenerSubastas(String perfilId, SubastasFiltro filtros) {
      PaginaResultado<Subasta> resultado = this.repoSubasta.buscarTodos(filtros, new CamposSubasta(true, true));

      if (filtros.participanteId() != null) {
        return resultado.mapearA(s -> {
          boolean yaCalifico = this.repoCalificacion.yaCalifico(
              perfilId,
              s.getAutor().getId(),
              s.getId(),
              MetodoIntercambio.SUBASTA
          );
          return new SubastaParticipoDto(s, obtenerOferta(perfilId, s), yaCalifico);
        });

      } else if ("ACTIVA".equals(filtros.estado())) {
        return resultado.mapearA(MiSubastaActivaDto::new);

      } else {
        return resultado.mapearA(s -> {
          String ganadorId = s.getOfertas().stream()
              .filter(o -> o.getEstadoActual().getValor() == EstadoProceso.ACEPTADO)
              .findFirst()
              .map(o -> o.getAutor().getId())
              .orElse(null);

          boolean yaCalifico = ganadorId != null && this.repoCalificacion.yaCalifico(
              ganadorId,
              perfilId,
              s.getId(),
              MetodoIntercambio.SUBASTA
          );

          return new MiSubastaFinalizadaDto(s, yaCalifico);
        });
      }
    }

  private Propuesta obtenerOferta(String perfilId, Subasta subasta) {
    return subasta.getOfertas().stream()
        .filter(p -> p.getAutor().getId().equals(perfilId))
        .findFirst()
        .orElseThrow(() -> new NotFoundException("No se encontró oferta del perfil " + perfilId + " en la subasta " + subasta.getId()));
  }

  public SubastaDto obtenerSubasta(String subastaId) {
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, new CamposSubasta(true, true));

    return new SubastaDto(subasta);
  }
}

