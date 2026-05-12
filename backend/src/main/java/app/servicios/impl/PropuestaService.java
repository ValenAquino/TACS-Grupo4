package app.servicios.impl;

import app.dto.PropuestaDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.propuesta.PropuestasDto;
import app.dto.request.CrearPropuestaRequest;
import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.Perfil;
import app.model.entities.Propuesta;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.servicios.INotificacionService;
import app.servicios.IPropuestaService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropuestaService implements IPropuestaService {

  private final RepositorioPropuestas repositorioPropuestas;
  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioFiguritasIntercambiables repositorioIntercambiables;
  private final INotificacionService notificacionService;

  /**
   * Crea una propuesta de intercambio. Valida que el usuario origen,
   * destino y figuritas existan. El estado inicial es PENDIENTE.
   */
  public PropuestaDto crearPropuesta(CrearPropuestaRequest request) {
    Perfil origen = repositorioPerfiles.buscarPorId(request.getAutorId());
    Perfil destino = repositorioPerfiles.buscarPorId(request.getDestinatarioId());

    if (origen == null) throw new NotFoundException("Usuario origen no encontrado");
    if (destino == null) throw new NotFoundException("Usuario destino no encontrado");

    Figurita figuritaBuscada = repositorioFiguritas
        .buscarPorId(request.getFiguritaBuscadaId());

    List<Figurita> figuritasOfrecidas = request.getFiguritasOfrecidasIds()
        .stream()
        .map(repositorioFiguritas::buscarPorId)
        .toList();

    Propuesta propuesta = new Propuesta(
        UUID.randomUUID().toString(),
        origen,
        destino,
        figuritasOfrecidas,
        figuritaBuscada
    );

    repositorioPropuestas.guardar(propuesta);

    String cuerpo = "Tenes una nueva propuesta de: " + origen.getNombre()
        + " por la figurita numero: " + figuritaBuscada.getNumero();

    notificacionService.notificarInteresados(List.of(destino), cuerpo);

    return new PropuestaDto(propuesta);
  }

  @Override
  public void aceptar(String propuestaId, String usuarioId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(propuestaId);
    Perfil perfil = repositorioPerfiles.buscarPorUsuarioId(usuarioId);
    propuesta.aceptar(perfil);
    repositorioPropuestas.guardar(propuesta);
  }

  @Override
  public void rechazar(String id, String usuarioId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(id);
    Perfil perfil = repositorioPerfiles.buscarPorUsuarioId(usuarioId);
    propuesta.rechazar(perfil);
    repositorioPropuestas.guardar(propuesta);
  }

  public PropuestasDto buscarPropuestas(String userId, PropuestasFiltro filtros) {
    if(filtros.tipo().equals("RECIBIDAS")) {
      return this.repositorioPropuestas.buscarPorDestinatarioId(userId, filtros);
    } else if (filtros.tipo().equals("ENVIADAS")) {
      return this.repositorioPropuestas.buscarPorAutorId(userId, filtros);
    }

    return null;
  }
}