package app.dto;

import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Seleccion;
import java.util.ArrayList;
import jdk.jfr.Experimental;
import lombok.Getter;
import java.util.List;

@Getter
public class FiguritaIntercambiableDto {
  private String figuritaId;
  private Integer numero;
  private String jugador;
  private Seleccion seleccion;
  private Integer cantidadExistente;
  private Integer cantidadReservada;
  private MetodoIntercambio metodo;
  @Experimental
  private String usuarioId;

  public FiguritaIntercambiableDto(FiguritaIntercambiable f) {
    this.figuritaId = f.getFigurita().getId();
    this.numero = f.getFigurita().getNumero();
    this.jugador = f.getFigurita().getJugador();
    this.seleccion = f.getFigurita().getSeleccion();
    this.cantidadExistente = f.getCantidadExistente();
    this.cantidadReservada = f.getCantidadReservada();
    this.metodo = f.getMetodo();
    this.usuarioId = f.getPerfilId();
  }
}