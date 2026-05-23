package app.servicios;

import app.dto.PropuestaDto;
import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.dto.request.CrearPropuestaRequest;
import app.model.entities.Figurita;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Propuesta;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioPropuesta {

  private final RepositorioPropuestas repositorioPropuestas;
  private final RepositorioPerfiles repositorioPerfiles;
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioColecciones repositorioColecciones;
  private final ServicioNotificacion notificacionService;

  /**
   * Crea una propuesta de intercambio. Valida que el usuario origen,
   * destino y figuritas existan. El estado inicial es PENDIENTE.
   */
  //TODO: Agregar transaccion para crear propuesta.
  @Transactional
  public PropuestaDto crearPropuesta(String autorId, CrearPropuestaRequest request) {
    CamposPerfil sinCampos = new CamposPerfil(false);

    Perfil autor = repositorioPerfiles.buscarPorId(autorId, sinCampos);
    Perfil destino = repositorioPerfiles.buscarPorId(request.getDestinatarioId(), sinCampos);

    Figurita figuritaBuscada = repositorioFiguritas
        .buscarPorId(request.getFiguritaBuscadaId());

    List<Figurita> figuritasOfrecidas = request.getFiguritasOfrecidasIds()
        .stream()
        .map(repositorioFiguritas::buscarPorId)
        .toList();

    autor.getColeccion().reservarRepetidas(figuritasOfrecidas, MetodoIntercambio.INTERCAMBIO);

    Propuesta propuesta = Propuesta.builder()
        .autor(autor)
        .destinatario(destino)
        .figuritaBuscada(figuritaBuscada)
        .figuritasOfrecidas(figuritasOfrecidas)
        .build();

    repositorioColecciones.guardar(autor.getColeccion());
    repositorioPropuestas.guardar(propuesta);

    String cuerpo = "Tenes una nueva propuesta de: " + autor.getNombre()
        + " por la figurita numero: " + figuritaBuscada.getNumero();

    notificacionService.notificarInteresados(List.of(destino), cuerpo);

    return new PropuestaDto(propuesta);
  }

  @Transactional
  public void aceptar(String propuestaId, String perfilId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(propuestaId);

    Perfil autor = propuesta.getAutor();
    Perfil destinatario = propuesta.getDestinatario();

    destinatario.getColeccion()
        .reservarRepetidas(
            List.of(propuesta.getFiguritaBuscada()),
            MetodoIntercambio.INTERCAMBIO
        );

    propuesta.aceptar(perfilId);

    repositorioColecciones.guardar(autor.getColeccion());
    repositorioColecciones.guardar(destinatario.getColeccion());

    repositorioPropuestas.guardar(propuesta);

  }

  @Transactional
  public void rechazar(String id, String perfilId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(id);
    propuesta.rechazar(perfilId);

    Perfil autor = propuesta.getAutor();;

    repositorioColecciones.guardar(autor.getColeccion());

    repositorioPropuestas.guardar(propuesta);
  }

  @Transactional
  public void cancelar(String id, String perfilId) {
    Propuesta propuesta = repositorioPropuestas.buscarPorId(id);
    propuesta.cancelar(perfilId);

    Perfil autor = propuesta.getAutor();;

    repositorioColecciones.guardar(autor.getColeccion());

    this.repositorioPropuestas.guardar(propuesta);
  }

  public PaginaResultado<PropuestaDto> buscarPropuestas(String perfilId, PropuestasFiltro filtros) {
    PaginaResultado<Propuesta> resultado = this.repositorioPropuestas.buscarTodos(perfilId, filtros);

    return new PaginaResultado<>(
        resultado.contenido().stream().map(PropuestaDto::new).toList(),
        resultado.cantidadDeElementos(),
        resultado.cantidadDePaginas(),
        resultado.numero());
  }
}