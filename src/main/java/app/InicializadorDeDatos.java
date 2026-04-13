package app;

import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioUsuarios;
import org.springframework.boot.CommandLineRunner;

public class InicializadorDeDatos implements CommandLineRunner {
  private final RepositorioUsuarios repositorioUsuarios;
  private final RepositorioColecciones repositorioColecciones;
  private final RepositorioFiguritas repositorioFiguritas;

  public InicializadorDeDatos(RepositorioUsuarios repositorioUsuarios,
                              RepositorioColecciones repositorioColecciones,
                              RepositorioFiguritas repositorioFiguritas) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.repositorioColecciones = repositorioColecciones;
    this.repositorioFiguritas = repositorioFiguritas;
  }

  @Override
  public void run(String... args){
    Figurita Messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA);
  }

  private void cargarColecciones(){}

}
