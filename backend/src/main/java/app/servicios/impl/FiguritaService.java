package app.servicios.impl;

import app.dto.FiguritaIntercambiableDto;
import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.model.entities.filtros.FiguritasFiltro;
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

  public List<Figurita> buscarFiguritas(FiguritasFiltro filtros) {

    List<String> idsFiltrados = repositorioFiguritas
        .buscarConFiltros(filtros)
        .stream()
        .map(Figurita::getId)
        .toList();

    return this.repositorioFiguritas.buscarConFiltros(filtros);
  }
}