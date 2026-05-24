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
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioSubastas;
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
  private final ServicioNotificacion notificacionService;

  @Transactional
  public void crearSubasta(String userId, String figuritaId, Integer duracionEnHoras,
                           List<String> figuritasDeseadasIds, Integer calificacionMinima) {
    CamposPerfil sinCamposPerfil = new CamposPerfil(false);

    Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId, sinCamposPerfil);
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

    this.repoSubasta.guardar(nuevaSubasta);

    CamposPerfil conMedio = new CamposPerfil(true);
    List<Perfil> interesados = this.repositorioPerfiles
        .buscarPorFiguritaFaltante(figuritaSubastada, conMedio);

    this.notificacionService.notificarInteresados(
        interesados, "Encontramos una subasta de una figurita que te falta!");
  }

  @Transactional
  public void ofertarEnSubasta(String autorId,
                               String subastaId,
                               List<String> rawFiguritasId
  ) {
    Perfil autor = this.repositorioPerfiles.buscarPorId(autorId, new CamposPerfil(false));
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
        .figuritasOfrecidas(figuritasOfrecidas).build();

    subasta.agregarOferta(nuevaPropuesta);
    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void editarOfertaEnSubasta(String perfilId, String subastaId, String ofertaId, EditarOfertaRequest body){
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);
    Propuesta oferta = subasta.getOfertas().stream()
        .filter(o -> o.getId().equals(ofertaId))
        .findFirst().orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

    List<Figurita> nuevas_figuritas = this.repoFigurita.buscarPorIds(body.getFiguritasOfrecidasId());

    oferta.setFiguritasOfrecidas(nuevas_figuritas);
    oferta.resetearAPendiente(perfilId);
    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void cancelarOferta(String perfilId, String subastaId, String ofertaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    subasta.cancelarOferta(ofertaId, perfilId);

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

    subasta.rechazarOferta(ofertaId, perfilId);

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
    this.repoSubasta.guardar(subasta, camposSubasta);
  }

  @Transactional
  public void cerrarSubasta(String perfilId, String subastaId) {
    CamposSubasta camposSubasta = new CamposSubasta(false, true);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId, camposSubasta);
    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }
    subasta.cerrar(perfilId);
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

