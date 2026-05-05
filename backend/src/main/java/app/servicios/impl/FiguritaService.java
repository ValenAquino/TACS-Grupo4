package app.servicios.impl;

import app.dto.FiguritaIntercambiableDto;
import app.dto.PaginaResultado;
import app.exceptions.NotFoundException;
import app.model.entities.Figurita;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Seleccion;
import app.repositories.RepositorioFiguritas;
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
  private final RepositorioFiguritas repositorioFiguritas;
  private final RepositorioPerfiles repositorioPerfiles;

  public PaginaResultado<FiguritaIntercambiableDto> buscarFiguritas(
      Integer numero, Seleccion seleccion, String jugador,
      MetodoIntercambio tipo, int pagina, int tamanioPagina) {

    List<String> idsFiltrados;
    try {
      idsFiltrados = repositorioFiguritas
          .buscarConFiltros(numero, seleccion, jugador)
          .stream()
          .map(Figurita::getId)
          .toList();
    } catch (NotFoundException e) {
      return new PaginaResultado<>(List.of(), 0, 0, pagina);
    }

    List<FiguritaIntercambiableDto> todas = repositorioIntercambiables
        .buscarPorFiguritaIds(idsFiltrados)
        .stream()
        .filter(fi -> tipo == null || fi.soporta(tipo))
        .map(fi -> new FiguritaIntercambiableDto(fi, buscarPerfil(fi.getPerfilId())))
        .toList();

    int total = todas.size();
    int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / tamanioPagina);
    int fromIndex = Math.min(pagina * tamanioPagina, total);
    int toIndex = Math.min(fromIndex + tamanioPagina, total);

    return new PaginaResultado<>(todas.subList(fromIndex, toIndex), total, totalPages, pagina);
  }

  private Perfil buscarPerfil(String perfilId) {
    try {
      return repositorioPerfiles.buscarPorId(perfilId);
    } catch (NotFoundException e) {
      return null;
    }
  }
}