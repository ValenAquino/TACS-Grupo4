package app.dto.subasta;

import app.model.entities.Propuesta;
import app.model.entities.EstadoProceso;
import lombok.Getter;

@Getter
public class OfertaSubastaDto {
  private String id;
  private String usuario;
  private String iniciales;
  private double calificacion;
  private String label;
  private boolean seleccionada;

  public OfertaSubastaDto(Propuesta propuesta) {
    this.id = propuesta.getId();
    this.usuario = propuesta.getAutor().getNombre();
    this.iniciales = calcularIniciales(propuesta.getAutor().getNombre());
    this.calificacion = propuesta.getAutor().obtenerCalificacionMedia();
    this.label = propuesta.getFiguritasOfrecidas().stream()
        .map(f -> f.getJugador() + " #" + f.getNumero())
        .reduce((a, b) -> a + " + " + b)
        .orElse("");
    this.seleccionada = propuesta.obtenerEstadoActual().getValor() == EstadoProceso.SELECCIONADO;
  }

  private String calcularIniciales(String nombre) {
    String[] partes = nombre.trim().split("\\s+");
    if (partes.length == 1) return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
    return (partes[0].charAt(0) + "" + partes[1].charAt(0)).toUpperCase();
  }
}