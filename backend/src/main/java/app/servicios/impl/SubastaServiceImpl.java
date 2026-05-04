package app.servicios.impl;

import app.dto.FiguritaDto;
import app.dto.subasta.MiSubastaDto;
import app.dto.subasta.MisSubastasResponseDto;
import app.dto.subasta.OfertaSubastaDto;
import app.dto.SubastaParticipoDto;
import app.exceptions.BadRequestException;
import app.model.entities.EstadoProceso;
import app.model.entities.EstadoPropuesta;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.servicios.INotificacionService;
import app.servicios.ISubastaService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubastaServiceImpl implements ISubastaService {
  private final RepositorioSubastas repoSubasta;
  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioFiguritas repoFigurita;
  private final INotificacionService notificacionService;

  @Override
  public void crearSubasta(String userId, String figuritaId, Integer duracionEnHoras) {
    Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId);
    Figurita figuritaSubastada = this.repoFigurita.buscarPorId(figuritaId);

    LocalDateTime fechaInicio = LocalDateTime.now();
    LocalDateTime fechaFin = fechaInicio.plusMinutes(duracionEnHoras.longValue());

    Subasta nuevaSubasta = new Subasta(
        UUID.randomUUID().toString(),
        perfil,
        fechaInicio,
        fechaFin,
        figuritaSubastada
    );

    this.repoSubasta.guardar(nuevaSubasta);

    List<Perfil> interesados = this.repositorioPerfiles
        .buscarPorFiguritaFaltante(nuevaSubasta.getFiguritaSubastada());

    this.notificacionService.notificarInteresados(
        interesados, "Encontramos una subasta de una figurita que te falta!");
  }

  @Override
  public void ofertarEnSubasta(String userId, String perfilDestinoId, String subastaId, List<String> rawFiguritasId) {
    Perfil autor = this.repositorioPerfiles.buscarPorUsuarioId(userId);
    Perfil destinatario = this.repositorioPerfiles.buscarPorId(perfilDestinoId);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    if (rawFiguritasId.size() != rawFiguritasId.stream().distinct().count()) {
      throw new BadRequestException("Figuritas ofrecidas repetidas");
    }

    List<Figurita> figuritasOfrecidas = rawFiguritasId.stream()
        .map(this.repoFigurita::buscarPorId)
        .toList();

    Propuesta nuevaPropuesta = new Propuesta(
        UUID.randomUUID().toString(),
        autor,
        destinatario,
        figuritasOfrecidas,
        subasta.getFiguritaSubastada()
    );

    subasta.getOfertas().add(nuevaPropuesta);
    this.repoSubasta.guardar(subasta);
  }

  @Override
  public void seleccionarOferta(String subastaId, String ofertaId) {
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    subasta.getOfertas().stream()
        .filter(p -> p.obtenerEstadoActual().getValor() == EstadoProceso.SELECCIONADO)
        .findFirst()
        .ifPresent(p -> p.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE)));

    Propuesta propuesta = subasta.getOfertas().stream()
        .filter(p -> p.getId().equals(ofertaId))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

    propuesta.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.SELECCIONADO));
    this.repoSubasta.guardar(subasta);
  }

  @Override
  public void rechazarOferta(String subastaId, String ofertaId) {
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    Propuesta propuesta = subasta.getOfertas().stream()
        .filter(p -> p.getId().equals(ofertaId))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

    propuesta.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.RECHAZADO));
    this.repoSubasta.guardar(subasta);
  }

  @Override
  public void cancelarSubasta(String subastaId) {
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    subasta.getOfertas().forEach(p ->
        p.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.RECHAZADO))
    );
    subasta.setFechaCierre(LocalDateTime.now());
    this.repoSubasta.guardar(subasta);
  }

  @Override
  public void cerrarSubasta(String subastaId) {
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

    if (!subasta.estaActivo()) {
      throw new BadRequestException("La subasta ya cerro");
    }

    Propuesta seleccionada = subasta.getOfertas().stream()
        .filter(p -> p.obtenerEstadoActual().getValor() == EstadoProceso.SELECCIONADO)
        .findFirst()
        .orElseThrow(() -> new BadRequestException("No hay una oferta seleccionada"));

    subasta.getOfertas().forEach(p -> {
      EstadoProceso nuevoEstado = p.getId().equals(seleccionada.getId())
          ? EstadoProceso.ACEPTADO
          : EstadoProceso.RECHAZADO;
      p.getEstado().add(new EstadoPropuesta(LocalDateTime.now(), nuevoEstado));
    });

    subasta.setFechaCierre(LocalDateTime.now());
    this.repoSubasta.guardar(subasta);
  }

  @Override
  public MisSubastasResponseDto obtenerMisSubastas(String userId) {
    List<Subasta> misSubastas = this.repoSubasta.buscarPorAutorUserId(userId);

    List<MiSubastaDto> activas = misSubastas.stream()
        .filter(Subasta::estaActivo)
        .map(MiSubastaDto::new)
        .toList();

    List<MiSubastaDto> finalizadas = misSubastas.stream()
        .filter(s -> !s.estaActivo())
        .map(MiSubastaDto::new)
        .toList();

    return new MisSubastasResponseDto(activas, finalizadas);
  }

  @Override
  public List<SubastaParticipoDto> obtenerSubastasParticipo(String userId) {
    return this.repoSubasta.buscarDondeParticipa(userId).stream()
        .map(s -> {
          Propuesta tuOferta = s.getOfertas().stream()
              .filter(p -> p.getAutor().getUsuario().getId().equals(userId))
              .findFirst()
              .get();
          return new SubastaParticipoDto(s, tuOferta);
        })
        .toList();
  }
}