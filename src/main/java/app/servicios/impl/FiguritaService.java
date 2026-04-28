package app.servicios.impl;

import app.dto.FiguritaIntercambiableDto;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.servicios.IFiguritaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FiguritaService implements IFiguritaService {

  private final RepositorioFiguritasIntercambiables repositorioIntercambiables;
  private final RepositorioFiguritas repositorioFiguritas;

  public List<FiguritaIntercambiableDto> buscarFiguritas(
      Integer numero, Seleccion seleccion, String jugador) {

    List<String> idsFiltrados = repositorioFiguritas
        .buscarConFiltros(numero, seleccion, jugador)
        .stream()
        .map(Figurita::getId)
        .toList();

    return repositorioIntercambiables
        .buscarPorFiguritaIds(idsFiltrados)
        .stream()
        .map(FiguritaIntercambiableDto::new)
        .toList();
  }
}