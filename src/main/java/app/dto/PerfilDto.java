package app.dto;

import app.model.entities.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerfilDto {
  private String id;
  private String nombre;

  public PerfilDto(Perfil perfil) {
    this.id = perfil.getId();
    this.nombre = perfil.getNombre();
  }
}
