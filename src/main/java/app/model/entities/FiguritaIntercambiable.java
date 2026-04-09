package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FiguritaIntercambiable {
  public Figurita figurita;
  public Integer cantidadDisponible;
  public List<MetodoIntercambio> metodos;


}
