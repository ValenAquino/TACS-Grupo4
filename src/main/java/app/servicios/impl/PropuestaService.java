package app.servicios.impl;

import app.dto.PropuestaDto;
import app.dto.request.CrearPropuestaRequest;
import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.Propuesta;
import app.model.entities.Perfil;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioPerfiles;
import app.servicios.INotificacionService;
import app.servicios.IPropuestaService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class PropuestaService implements IPropuestaService {

  private final RepositorioPropuestas repositorioPropuestas;
  private final RepositorioPerfiles repositorioUsuarios;
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioFiguritasIntercambiables repositorioIntercambiables;
  private final INotificacionService notificacionService;

  public PropuestaService(RepositorioPropuestas repositorioPropuestas,
                          RepositorioPerfiles repositorioUsuarios,
                          RepositorioFiguritas repositorioFiguritas,
                          RepositorioFiguritasIntercambiables repositorioIntercambiables,
                          INotificacionService notificacionService) {
    this.repositorioPropuestas = repositorioPropuestas;
    this.repositorioUsuarios = repositorioUsuarios;
    this.repositorioFiguritas = repositorioFiguritas;
    this.repositorioIntercambiables = repositorioIntercambiables;
    this.notificacionService = notificacionService;
  }

  /**
   * Crea una propuesta de intercambio. Valida que el usuario origen,
   * destino y figuritas existan. El estado inicial es PENDIENTE.
   */
  public PropuestaDto crearPropuesta(CrearPropuestaRequest request) {
    Perfil origen  = repositorioUsuarios.buscarPorId(request.getAutorId());
    Perfil destino = repositorioUsuarios.buscarPorId(request.getDestinatarioId());

    if (origen  == null) throw new NotFoundException("Usuario origen no encontrado");
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

    return aDto(propuesta);
  }
  public void aceptar(String id) {
      Propuesta propuesta = repositorioPropuestas.buscarPorId(id);
      propuesta.aceptar(propuesta.getDestinatario());
      repositorioPropuestas.guardar(propuesta);
  }

  public void rechazar(String id) {
      Propuesta propuesta = this.repositorioPropuestas.buscarPorId(id);
      propuesta.rechazar(propuesta.getDestinatario());
      repositorioPropuestas.guardar(propuesta);
  }

  private PropuestaDto aDto(Propuesta p) {
    return new PropuestaDto(
        p.getId(),
        p.getAutor().getId(),
        p.getDestinatario().getId(),
        p.getFiguritaBuscada().getId(),
        p.getFiguritasOfrecidas().stream().map(Figurita::getId).toList(),
        p.obtenerEstadoActual().getValor()
    );
  }
}