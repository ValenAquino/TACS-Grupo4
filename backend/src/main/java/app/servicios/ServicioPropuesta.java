package app.servicios;

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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioPropuesta {

  private final RepositorioPropuestas repositorioPropuestas;
  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioFiguritasIntercambiables repositorioIntercambiables;
  private final ServicioNotificacion notificacionService;

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

  public void aceptar(String propuestaId, String perfilId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(propuestaId);
    Perfil perfil = repositorioPerfiles.buscarPorId(perfilId);
    propuesta.aceptar(perfil);
    repositorioPropuestas.guardar(propuesta);
  }

  public void rechazar(String id, String perfilId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(id);
    Perfil perfil = repositorioPerfiles.buscarPorId(perfilId);
    propuesta.rechazar(perfil);
    repositorioPropuestas.guardar(propuesta);
  }

  public void cancelar(String id, String perfilId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(id);
    Perfil perfil = repositorioPerfiles.buscarPorId(perfilId);
    propuesta.cancelar(perfil);
    this.repositorioPropuestas.guardar(propuesta);
  }

  public PropuestasDto buscarPropuestas(String perfilId, PropuestasFiltro filtros) {
    if(filtros.tipo().equals("RECIBIDAS")) {
      return this.repositorioPropuestas.buscarPorDestinatarioId(perfilId, filtros);
    } else if (filtros.tipo().equals("ENVIADAS")) {
      return this.repositorioPropuestas.buscarPorAutorId(perfilId, filtros);
    }

    return null;
  }
}