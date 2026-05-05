package app.servicios.impl;

import app.dto.request.MejorarOfertaRequest;
import app.dto.request.OfertarEnSubastaRequest;
import app.dto.subasta.MiSubastaDto;
import app.dto.subasta.MisSubastasResponseDto;
import app.dto.subasta.SubastaDto;
import app.dto.subasta.SubastaParticipoDto;
import app.dto.subasta.SubastasParticipoResponseDto;
import app.exceptions.BadRequestException;
import app.model.entities.EstadoProceso;
import app.model.entities.EstadoPropuesta;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
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
  public void crearSubasta(String userId, String figuritaId, Integer duracionEnHoras,
                           List<String> figuritasDeseadasIds, Integer calificacionMinima) {
    Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId);
    Figurita figuritaSubastada = this.repoFigurita.buscarPorId(figuritaId);

    List<Figurita> figuritasDeseadas = figuritasDeseadasIds.stream()
        .map(this.repoFigurita::buscarPorId)
        .toList();

    LocalDateTime fechaInicio = LocalDateTime.now();
    LocalDateTime fechaFin = fechaInicio.plusHours(duracionEnHoras.longValue());

    Subasta nuevaSubasta = new Subasta(
        UUID.randomUUID().toString(),
        perfil,
        fechaInicio,
        fechaFin,
        figuritaSubastada,
        figuritasDeseadas,
        calificacionMinima
    );

    this.repoSubasta.guardar(nuevaSubasta);

    List<Perfil> interesados = this.repositorioPerfiles
        .buscarPorFiguritaFaltante(figuritaSubastada);

    this.notificacionService.notificarInteresados(
        interesados, "Encontramos una subasta de una figurita que te falta!");
  }

  @Override
  public void ofertarEnSubasta(String userId, String subastaId, List<String> rawFiguritasId) {
    Perfil autor = this.repositorioPerfiles.buscarPorUsuarioId(userId);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

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

    Propuesta nuevaPropuesta = new Propuesta(
        UUID.randomUUID().toString(),
        autor,
        destinatario,
        figuritasOfrecidas,
        subasta.getFiguritaSubastada()
    );

    subasta.agregarOferta(nuevaPropuesta);
    this.repoSubasta.guardar(subasta);
  }

  @Override
  public void mejorarOfertaEnSubasta(String subastaId, String ofertaId, MejorarOfertaRequest body){
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);
    Propuesta oferta = subasta.getOfertas().stream()
        .filter(o -> o.getId().equals(ofertaId))
        .findFirst().orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

    List<Figurita> nuevas_figuritas = body.getFiguritasOfrecidasId().stream().map(
        this.repoFigurita::buscarPorId
    ).toList();

    oferta.setFiguritasOfrecidas(nuevas_figuritas);
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

    propuesta.seleccionar(subasta.getAutor());
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
  public SubastasParticipoResponseDto obtenerSubastasParticipo(String userId) {
    List<Subasta> subastas = this.repoSubasta.buscarDondeParticipa(userId);

    List<SubastaParticipoDto> activas = subastas.stream()
        .filter(Subasta::estaActivo)
        .map(s -> new SubastaParticipoDto(s, obtenerOferta(s, userId)))
        .toList();

    List<SubastaParticipoDto> finalizadas = subastas.stream()
        .filter(s -> !s.estaActivo())
        .map(s -> new SubastaParticipoDto(s, obtenerOferta(s, userId)))
        .toList();

    return new SubastasParticipoResponseDto(activas, finalizadas);
  }
  private Propuesta obtenerOferta(Subasta subasta, String userId) {
    return subasta.getOfertas().stream()
        .filter(p -> p.getAutor().getUsuario().getId().equals(userId))
        .findFirst()
        .get();
  }
  @Override
  public SubastaDto obtenerSubasta(String subastaId) {
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

    return new SubastaDto(subasta);
  }
}