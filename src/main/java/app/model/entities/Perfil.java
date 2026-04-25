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
    private String nombre;
    private Coleccion coleccion;
    private List<MedioDeContacto> mediosDeContacto;
    private List<Integer> calificaciones;

    public Float obtenerCalificacionMedia() {
        return (float) calificaciones.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }
}
