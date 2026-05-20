package app.model.entities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "usuarios")
public class Usuario {
  @Id
  private String id;
  private Rol rol;
  private String nombre;
  private String contrasenia;

  public Usuario (String nombre, String contrasenia, Rol rol) {
    this.nombre = nombre;
    this.contrasenia = contrasenia;
    this.rol = rol;
  }
}
