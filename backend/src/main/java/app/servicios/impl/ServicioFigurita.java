package app.servicios.impl;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.exceptions.NotFoundException;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioPerfiles;
import app.servicios.IServicioFigurita;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioFigurita implements IServicioFigurita {

  private final RepositorioFiguritasIntercambiables repositorioIntercambiables;
  private final RepositorioPerfiles repositorioPerfiles;

  public PaginaResultado<FiguritaIntercambiableDto> buscarFiguritas(
      Integer numero, Seleccion seleccion, String jugador,
      MetodoIntercambio tipo, int pagina, int tamanioPagina) {

    FiguritasFiltro filtros = new FiguritasFiltro(null, numero, seleccion, jugador, tipo);
    PaginaResultado<FiguritaIntercambiable> paginaRepo =
        repositorioIntercambiables.buscarConFiltros(filtros, pagina, tamanioPagina);

    List<FiguritaIntercambiableDto> contenido = paginaRepo.contenido().stream()
        .map(fi -> new FiguritaIntercambiableDto(fi, buscarPerfil(fi.getPerfilId())))
        .toList();

    return new PaginaResultado<>(contenido, paginaRepo.cantidadDeElementos(),
        paginaRepo.cantidadDePaginas(), paginaRepo.numero());
  }

  public PaginaResultado<FiguritaIntercambiableDto> buscarPorQuery(
      String q, MetodoIntercambio tipo, int pagina, int tamanioPagina) {

    PaginaResultado<FiguritaIntercambiable> paginaRepo =
        repositorioIntercambiables.buscarPorQuery(q, tipo, pagina, tamanioPagina);

    List<FiguritaIntercambiableDto> contenido = paginaRepo.contenido().stream()
        .map(fi -> new FiguritaIntercambiableDto(fi, buscarPerfil(fi.getPerfilId())))
        .toList();

    return new PaginaResultado<>(contenido, paginaRepo.cantidadDeElementos(),
        paginaRepo.cantidadDePaginas(), paginaRepo.numero());
  }

  private Perfil buscarPerfil(String perfilId) {
    try {
      return repositorioPerfiles.buscarPorId(perfilId);
    } catch (NotFoundException e) {
      return null;
    }
  }
}
