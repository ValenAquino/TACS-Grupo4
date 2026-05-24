package app.model.entities;

import app.exceptions.BadRequestException;
import java.time.LocalDateTime;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "subastas")
@Builder
public class Subasta {

    @Id
    private String id;

    @DBRef
    private Perfil autor;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;

    @DBRef
    private Figurita figuritaSubastada;

    @Builder.Default
    private List<Propuesta> ofertas = new ArrayList<>();;

    @DBRef
    @Builder.Default
    private List<Figurita> figuritasSolicitadas = new ArrayList<>();

    @Builder.Default
    private Integer calificacionMinimaSolicitada = 1;

    public void agregarOferta(Propuesta oferta) {
        boolean tieneCondicionesSolicitadas = !this.figuritasSolicitadas.isEmpty();
        boolean noOfertaLasSolicitadas = this.figuritasSolicitadas.stream().noneMatch(fs -> oferta.getFiguritasOfrecidas().contains(fs));

        if(tieneCondicionesSolicitadas && (noOfertaLasSolicitadas || oferta.getAutor().getCalificacionMedia() < this.calificacionMinimaSolicitada)) {
            throw new BadRequestException("No se cumplieron las condiciones minimas");
        }

        this.ofertas.add(oferta);
    }

    /**
     * Retorna {@code true} si la subasta está dentro de su ventana de tiempo
     */
    public Boolean estaActivo() {
        final LocalDateTime fechaActual = LocalDateTime.now();

        return fechaActual.isAfter(fechaInicio) && fechaActual.isBefore(fechaCierre);
    }

    public void cancelar(String perfilId){
        Propuesta seleccionada = obtenerSeleccionada();
        if(seleccionada != null) {
            seleccionada.rechazar(perfilId);
        }

        rechazarOfertasPendientes(perfilId);
        finalizarSubasta();
    }

    public void cerrar(String perfilId) {
        Propuesta seleccionada = obtenerSeleccionada();
        if(seleccionada != null) {
            seleccionada.aceptar(perfilId);
        }

        rechazarOfertasPendientes(perfilId);
        finalizarSubasta();
    }

    public void seleccionarOferta(String ofertaId, String perfilId){
        Propuesta oferta = this.getOfertas().stream()
            .filter(p -> p.getId().equals(ofertaId))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

        this.getOfertas().stream()
            .filter(p -> p.getEstadoActual().getValor() == EstadoProceso.SELECCIONADO)
            .findFirst()
            .ifPresent(p -> p.deseleccionar(perfilId));

        oferta.seleccionar(perfilId);
    }

    public void cancelarOferta(String ofertaId, String perfilId){
        Propuesta oferta = getOfertas().stream()
            .filter(o -> o.getId().equals(ofertaId))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

        oferta.cancelar(perfilId);
    }

    public void rechazarOferta(String ofertaId, String perfilId){
        Propuesta oferta = this.getOfertas().stream()
            .filter(p -> p.getId().equals(ofertaId))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Oferta no encontrada"));

        oferta.rechazar(perfilId);
    }

    private void rechazarOfertasPendientes(String perfilId) {
        getOfertas().stream()
            .filter(o -> o.getEstadoActual().getValor() == EstadoProceso.PENDIENTE)
            .forEach(o -> o.rechazar(perfilId));
    }

    private void finalizarSubasta() {
        this.setFechaCierre(LocalDateTime.now());
    }

    Propuesta obtenerSeleccionada() {
        return getOfertas().stream()
            .filter(p -> p.getEstadoActual().getValor() == EstadoProceso.SELECCIONADO)
            .findFirst()
            .orElse(null);
    }
}

