package app.dto;

import app.model.entities.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PerfilDto {
  private String id;
  private String usuarioId;
  private String nombre;
  private Number calificacion;
  private String iniciales;

  public PerfilDto(Perfil perfil) {
    this.usuarioId = perfil.getUsuario().getId();
    this.id = perfil.getId();
    this.nombre = perfil.getNombre();
    this.calificacion = perfil.getCalificacionMedia();
    this.iniciales = calcularIniciales(nombre);
  }

  private String calcularIniciales(String nombre) {
    String[] partes = nombre.trim().split("\\s+");
    if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
    return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
  }
}
