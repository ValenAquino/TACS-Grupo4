package app.model.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "perfiles")
public class Perfil {

    @Id
    private String id;

    @DBRef
    private Usuario usuario;
    private String nombre;

    @DBRef
    @Builder.Default
    private Coleccion coleccion = new Coleccion();

    @Builder.Default
    private List<MedioDeContacto> mediosDeContacto = new ArrayList<>();;

    @DBRef
    @Builder.Default
    private List<Calificacion> calificaciones = new ArrayList<>();

    @Builder.Default
    private Double calificacionMedia = 0.0;
    private int cantidadCalificaciones = 0;

    /**
     * Calcula el promedio de las calificaciones recibidas.
     * Retorna 0 si el perfil aún no tiene calificaciones.
     */
    public void calcularCalificacionMedia(int calificacion) {
        this.calificacionMedia = (calificacionMedia + calificacion)/ (this.cantidadCalificaciones + 1);
    }

    public void agregarNuevaCalificacion(Calificacion calificacion){
        this.calificaciones.add(calificacion);
        this.cantidadCalificaciones++;
        this.calcularCalificacionMedia(calificacion.getValor());
    }
}
