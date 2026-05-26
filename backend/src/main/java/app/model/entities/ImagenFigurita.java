package app.model.entities;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "imagenes_figuritas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenFigurita {

  @Id
  private String figuritaId;

  private String imagenUrl;

  private String status;

  private LocalDateTime creadoEn;
}
