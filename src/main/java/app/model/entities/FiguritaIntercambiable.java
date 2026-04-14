package app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FiguritaIntercambiable {
    private Figurita figurita;
    private Integer cantidadDisponible;
    private List<MetodoIntercambio> metodos;
    private String usuarioId;
    public FiguritaIntercambiable(Figurita figurita, Integer cantidadDisponible, List<MetodoIntercambio> metodos) {
        this(figurita, cantidadDisponible, metodos, null);
    }
}
