package app.model.entities;

import app.exceptions.BadRequestException;
import jdk.jfr.Experimental;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FiguritaIntercambiable {

    @DBRef
    private Figurita figurita;

    private Integer cantidadExistente;
    @Builder.Default
    private Integer cantidadReservada = 0;
    private List<MetodoIntercambio> metodos;

    @Experimental
    @Builder.Default
    private String perfilId = null;

    public FiguritaIntercambiable(Figurita figurita, Integer cantidadExistente, List<MetodoIntercambio> metodos) {
        this(figurita, cantidadExistente,0, metodos, null);
    }

    public FiguritaIntercambiable(Figurita figurita, Integer cantidadExistente, List<MetodoIntercambio> metodos, String perfilId) {
        this(figurita, cantidadExistente,0, metodos, perfilId);
    }

    public boolean soporta(MetodoIntercambio tipo) {
        return metodos.contains(tipo);
    }

    public int getCantidadDisponible() {
        return this.cantidadExistente - this.cantidadReservada;
    }

    public void reservar(MetodoIntercambio metodo) {
        this.validarMetodo(metodo);
        this.validarCantidadDisponible();
        this.cantidadReservada += 1;
    }

    public void eliminarReserva() {
        if (cantidadReservada <= 0) {
            throw new BadRequestException("No hay reservas para eliminar");
        }
        this.cantidadReservada -= 1;
    }

    public void cambioConcretado() {
        if (cantidadExistente <= 0) {
            throw new BadRequestException("No hay cantidad existente de esta figurita");
        }

        this.cantidadExistente -= 1;

        if (cantidadReservada > 0) {
            eliminarReserva();
        }
    }

    private void validarMetodo(MetodoIntercambio metodo) {
        if(!this.metodos.contains(metodo)) {
            throw new BadRequestException("Esta figurita no soporta el metodo seleccionado");
        }
    }

    private void validarCantidadDisponible() {
        if (this.getCantidadDisponible() <= 0) {
            throw new BadRequestException("No hay figuritas disponibles para reservar");
        }
    }
}
