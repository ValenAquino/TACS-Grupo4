package app.model.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter
public class Subasta {
    private Usuario usuario;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;
    private Figurita figuritaSubastada;
    private Propuesta propuestaGanadora;

    public Boolean estaActivo() {
        final LocalDateTime fechaActual = LocalDateTime.now();

        return fechaActual.isAfter(fechaInicio) && fechaActual.isBefore(fechaCierre);
    }

    public Boolean algoritmoSeleccionador(Propuesta propuesta) {
        Propuesta propuestaActual = this.propuestaGanadora;

        //Dejo la funcion por si en futuras entregas se pone una rareza de figuritas

        return propuesta.getFiguritasOfrecidas().size() > propuestaActual.getFiguritasOfrecidas().size();
    }

    void setPropuestaGanadora(Propuesta propuesta) {
        if(this.algoritmoSeleccionador(propuesta)) {
            this.propuestaGanadora = propuesta;
        }
    }
}
