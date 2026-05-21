package app.servicios;

import app.dto.paginacion.PaginaResultado;
import app.dto.request.MejorarOfertaRequest;
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
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

  @Service
  @RequiredArgsConstructor
  public class ServicioSubasta {
    private final RepositorioSubastas repoSubasta;
    private final RepositorioPerfiles repositorioPerfiles;
    private final RepositorioFiguritas repoFigurita;
    private final ServicioNotificacion notificacionService;

    public void crearSubasta(String userId, String figuritaId, Integer duracionEnHoras,
                             List<String> figuritasDeseadasIds, Integer calificacionMinima) {
      Perfil perfil = this.repositorioPerfiles.buscarPorUsuarioId(userId);
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

      List<Perfil> interesados = this.repositorioPerfiles
          .buscarPorFiguritaFaltante(figuritaSubastada);

      this.notificacionService.notificarInteresados(
          interesados, "Encontramos una subasta de una figurita que te falta!");
    }

    public void ofertarEnSubasta(String autorId,
                                 String subastaId,
                                 List<String> rawFiguritasId
    ) {
      Perfil autor = this.repositorioPerfiles.buscarPorId(autorId);
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

      Propuesta nuevaPropuesta = Propuesta.builder()
          .autor(autor)
          .destinatario(destinatario)
          .figuritaBuscada(subasta.getFiguritaSubastada())
          .figuritasOfrecidas(figuritasOfrecidas).build();

      subasta.agregarOferta(nuevaPropuesta);
      this.repoSubasta.guardar(subasta);
    }

    public void mejorarOfertaEnSubasta(String subastaId, String ofertaId, MejorarOfertaRequest body){
      Subasta subasta = this.repoSubasta.buscarPorId(subastaId);
      Propuesta oferta = subasta.getOfertas().stream()
          .filter(o -> o.getId().equals(ofertaId))
          .findFirst().orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

      List<Figurita> nuevas_figuritas = body.getFiguritasOfrecidasId().stream().map(
          this.repoFigurita::buscarPorId
      ).toList();

      oferta.setFiguritasOfrecidas(nuevas_figuritas);
      this.repoSubasta.guardar(subasta);
    }

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

      propuesta.seleccionar(subasta.getAutor().getId());
      this.repoSubasta.guardar(subasta);
    }

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

    public PaginaResultado<SubastaDto> obtenerMisSubastas(String perfilId, Integer pagina, Integer limite) {
      PaginaResultado<Subasta> resultado = this.repoSubasta.buscarPorAutor(perfilId, pagina, limite);

      return new PaginaResultado<>(
          resultado.contenido().stream().map(SubastaDto::new).toList(),
          resultado.cantidadDeElementos(),
          resultado.cantidadDePaginas(),
          resultado.numero());
    }

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

    public SubastaDto obtenerSubasta(String subastaId) {
      Subasta subasta = this.repoSubasta.buscarPorId(subastaId);

      return new SubastaDto(subasta);
    }
  }

