package app.servicios.impl;

import app.dto.SubastaDto;
import app.exceptions.BadRequestException;
import app.model.entities.EstadoProceso;
import app.model.entities.Figurita;
import app.model.entities.Propuesta;
import app.model.entities.Subasta;
import app.model.entities.Perfil;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioPerfiles;
import app.servicios.INotificacionService;
import app.servicios.ISubastaService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubastaServiceImpl implements ISubastaService {
  private final RepositorioSubastas repoSubasta;
  private final RepositorioPerfiles repoUsuario;
  private final RepositorioFiguritas repoFigurita;
  private final RepositorioPropuestas repoPropuesta;
  private final INotificacionService notificacionService;

  public SubastaServiceImpl(RepositorioSubastas repoSubasta, RepositorioPerfiles repoUsuario,
                            RepositorioFiguritas repoFigurita, RepositorioPropuestas repoPropuesta,
                            INotificacionService notificacionService) {
    this.repoSubasta = repoSubasta;
    this.repoUsuario = repoUsuario;
    this.repoFigurita = repoFigurita;
    this.repoPropuesta = repoPropuesta;
    this.notificacionService = notificacionService;
  }

  @Override
  public SubastaDto crearSubasta(String userId, LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                 String figuritaId, Propuesta propuestaGanadora) {
    Perfil usuario = this.repoUsuario.buscarPorId(userId);
    Figurita figuritaSubastada = this.repoFigurita.buscarPorId(figuritaId);
    Subasta nuevaSubasta = new Subasta(
        usuario, fechaInicio, fechaFin,
        figuritaSubastada, propuestaGanadora);

    this.repoSubasta.guardar(nuevaSubasta);

    List<Perfil> interesados = this.repoUsuario.buscarPorFiguritaFaltante(nuevaSubasta.getFiguritaSubastada());

    this.notificacionService.notificarInteresados(interesados,"Encontramos una subasta de una figurita que te falta!");

    return new SubastaDto(nuevaSubasta);
  }

  @Override
  public SubastaDto ofertarEnSubasta(String userId, String usuarioDestinoId,
                               String subastaId, List<Object> rawFiguritasId) {
    Perfil usuarioOrigen = this.repoUsuario.buscarPorId(userId);
    Perfil usuarioDestino = this.repoUsuario.buscarPorId(usuarioDestinoId);
    Subasta subasta = this.repoSubasta.buscarPorId(subastaId);
    List<Figurita> figuritasOfrecidas = new ArrayList<>();

    Figurita figuritaBuscada = subasta.getFiguritaSubastada();

    if (rawFiguritasId.size() != rawFiguritasId.stream().distinct().count()) {
      //El listado debe tener figuritas distintas
      throw new BadRequestException("Figuritas ofrecidas repetidas");
    }

    rawFiguritasId.forEach(figuritaId -> {
      Figurita figurita = this.repoFigurita.buscarPorId((String) figuritaId);
      figuritasOfrecidas.add(figurita);
    });

    Propuesta nuevaPropuesta = new Propuesta(usuarioOrigen, usuarioDestino, figuritasOfrecidas, figuritaBuscada, EstadoProceso.PENDIENTE);

    this.repoPropuesta.guardar(nuevaPropuesta);

    subasta.algoritmoSeleccionador(nuevaPropuesta);

    this.repoSubasta.guardar(subasta);

    return new SubastaDto(subasta);
  }
}
