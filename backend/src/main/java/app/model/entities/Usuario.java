package app.model.entities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Document(collection = "usuarios")
public class Usuario {
  @Id
  private String id;
  private Rol rol;
}
