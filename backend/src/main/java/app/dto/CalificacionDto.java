package app.dto;

import app.model.entities.Calificacion;
import lombok.Getter;

@Getter
public class CalificacionDto {
  private String id;
  private String autorId;
  private String nombre;
  private String iniciales;
  private Integer valor;
  private String descripcion;

  public CalificacionDto(Calificacion c) {
    this.id = c.getId();
    this.autorId = c.getAutor().getId();
    this.nombre = c.getAutor().getNombre();
    this.iniciales = c.getAutor().getIniciales();
    this.valor = c.getValor();
    this.descripcion = c.getDescripcion();
  }
}
