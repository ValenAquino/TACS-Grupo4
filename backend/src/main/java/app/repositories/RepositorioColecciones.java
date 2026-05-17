package app.repositories;

import app.dto.FaltantesDto;
import app.dto.Repetidas;
import app.model.entities.Coleccion;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.filtros.FaltantesFiltro;
import app.model.entities.filtros.RepetidasFiltro;

public interface RepositorioColecciones {

  public Coleccion buscarPorId(String colId);

  public void guardar(Coleccion coleccion);

  public Repetidas<FiguritaIntercambiable> buscarRepetidas(String colId, RepetidasFiltro filtros);

  public FaltantesDto buscarFaltantes(String colId, FaltantesFiltro filtros);
}
