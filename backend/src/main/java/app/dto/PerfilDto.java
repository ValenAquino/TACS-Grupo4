package app.dto;

import app.model.entities.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PerfilDto {
  private String id;
  private String nombre;
  private Number puntuacion;

  public PerfilDto(Perfil perfil) {
    this.id = perfil.getId();
    this.nombre = perfil.getNombre();
    this.puntuacion = perfil.obtenerCalificacionMedia();
  }
}
