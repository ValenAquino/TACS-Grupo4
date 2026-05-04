package app.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Subasta {
    private String id;
    private Perfil autor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;
    private Figurita figuritaSubastada;
    private List<Propuesta> ofertas;
    private List<Figurita> figuritasSolicitadas;
    private Integer calificacionMinimaSolicitada;
    private Boolean finalizada;

    public Subasta(String id, Perfil autor, LocalDateTime fechaInicio, LocalDateTime fechaCierre,
                   Figurita figuritaSubastada, List<Figurita> figuritasSolicitadas,
                   Integer calificacionMinimaSolicitada) {
        this.id = id;
        this.autor = autor;
        this.fechaInicio = fechaInicio;
        this.fechaCierre = fechaCierre;
        this.figuritaSubastada = figuritaSubastada;
        this.ofertas = new ArrayList<>();
        this.figuritasSolicitadas = figuritasSolicitadas;
        this.calificacionMinimaSolicitada = calificacionMinimaSolicitada;
    }

    public Subasta(String id, Perfil autor, LocalDateTime fechaInicio, LocalDateTime fechaCierre,
                   Figurita figuritaSubastada) {
        this.id = id;
        this.autor = autor;
        this.fechaInicio = fechaInicio;
        this.fechaCierre = fechaCierre;
        this.figuritaSubastada = figuritaSubastada;
        this.ofertas = new ArrayList<>();
        this.figuritasSolicitadas = new ArrayList<>();
        this.calificacionMinimaSolicitada = 0;
    }

    /**
     * Retorna {@code true} si la subasta está dentro de su ventana de tiempo
     */
    public Boolean estaActivo() {
        final LocalDateTime fechaActual = LocalDateTime.now();

        return fechaActual.isAfter(fechaInicio) && fechaActual.isBefore(fechaCierre);
    }

}

