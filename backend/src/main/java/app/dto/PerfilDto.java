package app.dto;

import app.model.entities.MedioDeContacto;
import app.model.entities.Perfil;
import java.util.List;
import lombok.Getter;

@Getter
public class PerfilDto {
  private String id;
  private String usuarioId;
  private String nombre;
  private String nombreUsuario;
  private String iniciales;
  private Number calificacionMedia;
  private List<MedioDeContacto> mediosDeContacto;

  public PerfilDto(Perfil perfil) {
    this.id = perfil.getId();
    this.nombre = perfil.getNombre();
    this.iniciales = calcularIniciales(nombre);
    this.calificacionMedia = perfil.getCalificacionMedia();
    this.mediosDeContacto = perfil.getMediosDeContacto();

    if (perfil.getUsuario() != null) {
        this.usuarioId = perfil.getUsuario().getId();
        this.nombreUsuario = perfil.getUsuario().getNombre();
    }
  }


  private String calcularIniciales(String nombre) {
    String[] partes = nombre.trim().split("\\s+");
    if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
    return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
  }
}
