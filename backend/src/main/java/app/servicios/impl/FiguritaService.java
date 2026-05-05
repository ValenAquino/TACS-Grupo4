package app.servicios.impl;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.exceptions.NotFoundException;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Seleccion;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioPerfiles;
import app.servicios.IFiguritaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FiguritaService implements IFiguritaService {

  private final RepositorioFiguritasIntercambiables repositorioIntercambiables;
  private final RepositorioPerfiles repositorioPerfiles;

  public PaginaResultado<FiguritaIntercambiableDto> buscarFiguritas(
      Integer numero, Seleccion seleccion, String jugador,
      MetodoIntercambio tipo, int pagina, int tamanioPagina) {

    PaginaResultado<FiguritaIntercambiable> paginaRepo =
        repositorioIntercambiables.buscarConFiltros(numero, seleccion, jugador, tipo, pagina, tamanioPagina);

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
