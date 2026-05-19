package app.model.entities;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "perfiles")
public class Perfil {
    @Id
    private String id;
    @DBRef
    private Usuario usuario;
    private String nombre;
    private Coleccion coleccion;
    private List<MedioDeContacto> mediosDeContacto;
    private List<Calificacion> calificaciones;

    public Perfil(Usuario usuario,
                  String nombre,
                  Coleccion coleccion,
                  List<MedioDeContacto> mediosDeContacto,
                  List<Calificacion> calificaciones) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.coleccion = coleccion;
        this.mediosDeContacto = mediosDeContacto;
        this.calificaciones = calificaciones;
    }

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

    public String getIniciales() {
        return this.nombre.substring(0, 2).toUpperCase();
    }
}
