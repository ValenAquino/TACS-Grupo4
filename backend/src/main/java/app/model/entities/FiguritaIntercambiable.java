package app.model.entities;

import jdk.jfr.Experimental;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FiguritaIntercambiable {
    private Figurita figurita;
    private Integer cantidadExistente;
    private Integer cantidadReservada;
    private MetodoIntercambio metodo;
    @Experimental
    private String perfilId;
    public FiguritaIntercambiable(Figurita figurita, Integer cantidadDisponible, MetodoIntercambio metodo) {
        this(figurita, cantidadDisponible,0, metodo, null);
    }

    public FiguritaIntercambiable(Figurita figurita, Integer cantidadDisponible, MetodoIntercambio metodo, String perfilId) {
        this(figurita, cantidadDisponible,0, metodo, perfilId);
    }

    public boolean hayCantidadDisponible(){
      return this.cantidadExistente - this.cantidadReservada != 0;
    }

    public int getCantidadDisponible() {
        return this.cantidadExistente - this.cantidadReservada;
    }

    public void reservarFiguritaIntercambiable() {
        if (cantidadExistente - cantidadReservada <= 0) {
            throw new RuntimeException("No hay figuritas disponibles para reservar");
        }
        this.cantidadReservada += 1;
    }

    public void eliminarReserva() {
        if (cantidadReservada <= 0) {
            throw new RuntimeException("No hay reservas para eliminar");
        }
        this.cantidadReservada -= 1;
    }

    public void cambioConcretado() {
        if (cantidadExistente <= 0) {
            throw new RuntimeException("No hay stock disponible");
        }

        this.cantidadExistente -= 1;

        if (cantidadReservada > 0) {
            eliminarReserva();
        }
    }
}
