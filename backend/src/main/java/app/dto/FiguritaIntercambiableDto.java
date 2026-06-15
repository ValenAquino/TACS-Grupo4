package app.dto;

import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import app.repositories.projections.ResumenPerfil;
import java.util.List;
import lombok.Getter;

@Getter
public class FiguritaIntercambiableDto {
  private String figuritaId;
  private Integer numero;
  private String jugador;
  private Seleccion seleccion;
  private Integer cantidadExistente;
  private Integer cantidadReservada;
  private List<MetodoIntercambio> metodos;
  private String perfilId;
  private String nombreUsuario;
  private Integer reputacion;
  private String posicion;
  private String imagenUrl;
  private String subastaId;

  public FiguritaIntercambiableDto(FiguritaIntercambiable f, ResumenPerfil perfil, String subastaId) {
    this.figuritaId = f.getFigurita().getId();
    this.numero = f.getFigurita().getNumero();
    this.jugador = f.getFigurita().getJugador();
    this.seleccion = f.getFigurita().getSeleccion();
    this.posicion = f.getFigurita().getPosicion();
    this.imagenUrl = f.getFigurita().getImagenUrl();
    this.cantidadExistente = f.getCantidadExistente();
    this.cantidadReservada = f.getCantidadReservada();
    this.metodos = f.getMetodos();
    this.perfilId = f.getPerfilId();
    this.nombreUsuario = perfil != null ? perfil.nombre() : null;
    this.reputacion = perfil != null ? (int) Math.round(perfil.calificacionMedia()) : null;
    this.subastaId = subastaId;
  }

  public FiguritaIntercambiableDto(FiguritaIntercambiable f, ResumenPerfil perfil) {
    this(f, perfil, null);
  }

  public FiguritaIntercambiableDto(FiguritaIntercambiable f) {
    this(f, null, null);
  }
}
