package app.model.entities;

import jdk.jfr.Experimental;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FiguritaIntercambiable {

    @DBRef
    private Figurita figurita;

    private Integer cantidadExistente;
    private Integer cantidadReservada;
    private List<MetodoIntercambio> metodos;

    @Experimental
    private String perfilId;

    public FiguritaIntercambiable(Figurita figurita, Integer cantidadExistente, List<MetodoIntercambio> metodos) {
        this(figurita, cantidadExistente,0, metodos, null);
    }

    public FiguritaIntercambiable(Figurita figurita, Integer cantidadExistente, List<MetodoIntercambio> metodos, String perfilId) {
        this(figurita, cantidadExistente,0, metodos, perfilId);
    }

    public boolean soporta(MetodoIntercambio tipo) {
        return metodos.contains(tipo);
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
