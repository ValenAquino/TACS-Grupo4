package app.model.entities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Perfil {
    private String id;
    private Usuario usuario;
    private String nombre;
    private Coleccion coleccion;
    private List<MedioDeContacto> mediosDeContacto;
    private List<Calificacion> calificaciones;

    /**
     * Calcula el promedio de las calificaciones recibidas.
     * Retorna 0 si el perfil aún no tiene calificaciones.
     */
    public double obtenerCalificacionMedia() {
        return calificaciones.stream()
            .mapToInt(Calificacion::getValor)
            .average()
            .orElse(0.0);
    }

    public void agregarNuevaCalificacion(Calificacion calificacion){
        this.calificaciones.add(calificacion);
    }
}
