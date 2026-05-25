package app.dto;

import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Seleccion;
import java.util.List;
import lombok.Getter;

@Getter
public class FiguritaIntercambiableDto {
  private final String figuritaId;
  private final Integer numero;
  private final String jugador;
  private final Seleccion seleccion;
  private final Integer cantidadExistente;
  private final Integer cantidadReservada;
  private final List<MetodoIntercambio> metodos;
  private final String perfilId;
  private final String nombreUsuario;
  private final Integer reputacion;
  private final String posicion;
  private final String imagenUrl;

  public FiguritaIntercambiableDto(FiguritaIntercambiable f, Perfil perfil) {
    this.figuritaId = f.getFigurita().getId();
    this.numero = f.getFigurita().getNumero();
    this.jugador = f.getFigurita().getJugador();
    this.posicion = f.getFigurita().getPosicion();
    this.seleccion = f.getFigurita().getSeleccion();
    this.imagenUrl = f.getFigurita().getImagenUrl();
    this.cantidadExistente = f.getCantidadExistente();
    this.cantidadReservada = f.getCantidadReservada();
    this.metodos = f.getMetodos();
    this.perfilId = f.getPerfilId();
    this.nombreUsuario = perfil != null ? perfil.getNombre() : null;
    this.reputacion = perfil != null ? (int) Math.round(perfil.getCalificacionMedia()) : null;
  }

  public FiguritaIntercambiableDto(FiguritaIntercambiable f) {
    this(f, null);
  }
}