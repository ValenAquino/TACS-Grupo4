package app.model.entities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "usuarios")
public class Usuario {
  @Id
  private String id;
  private Rol rol;
  private String nombre;
  private String contrasenia;

  public Usuario() {}

  public Usuario (String nombre, String contrasenia, Rol rol) {
    this.nombre = nombre;
    this.contrasenia = contrasenia;
    this.rol = rol;
  }

  public Usuario (String id, Rol rol, String nombre, String contrasenia) {
    this.id = id;
    this.nombre = nombre;
    this.contrasenia = contrasenia;
    this.rol = rol;
  }
}
