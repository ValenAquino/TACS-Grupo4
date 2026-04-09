package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Propuesta {

  public Usuario usuarioOrigen;
  public Usuario usuarioDestino;
  public List<Figurita> figuritasOfrecidas;
  public Figurita figuritaBuscada;
  public EstadoProceso estado;


}
