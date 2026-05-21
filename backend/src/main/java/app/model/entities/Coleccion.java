package app.model.entities;

import app.exceptions.BadRequestException;
import app.exceptions.FiguritaDuplicadaException;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "colecciones")
public class Coleccion {
  @Id
  private String id;

  @DBRef
  private List<Figurita> faltantes = new ArrayList<Figurita>();

  private List<FiguritaIntercambiable> repetidas = new ArrayList<FiguritaIntercambiable>();

  public Coleccion(String id) {
    this.id = id;
  }

  /**
   * Agrega una figurita a la lista de faltantes.
   * Lanza {@link BadRequestException} si ya está registrada como faltante.
   */
  public void agregarFaltante(Figurita faltante) {
    if (tieneFaltante(faltante)) {
      throw new BadRequestException("Figurita ya listada como faltante");
    }

    this.faltantes.add(faltante);
  }

  /**
   * Agrega una figurita repetida a la colección. Si ya existe una entrada para
   * esa figurita, acumula la cantidad en lugar de crear una entrada duplicada.
   */
  public void agregarRepetida(FiguritaIntercambiable repetida) {

    for (FiguritaIntercambiable f : repetidas) {
      if (f.getFigurita().getId()
          .equals(repetida.getFigurita().getId())) {

        f.setCantidadExistente(f.getCantidadExistente() + repetida.getCantidadExistente());
        return;
      }
    }

    repetidas.add(repetida);
  }

  public boolean tieneFaltante(Figurita figurita) {
    return !this.faltantes.stream().filter(p -> p.getId().equals(figurita.getId())).toList().isEmpty();
  }
}
