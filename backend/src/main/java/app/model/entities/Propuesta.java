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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "propuestas")
@Builder
public class Propuesta {

    @Id
    private String id;

    @DBRef
    private Perfil autor;

    @DBRef
    private Perfil destinatario;

    @DBRef
    private List<Figurita> figuritasOfrecidas;

    @DBRef
    private Figurita figuritaBuscada;

    @Builder.Default
    private List<EstadoPropuesta> estado = new ArrayList<>(
        List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))
    );

    @Builder.Default
    private EstadoPropuesta estadoActual = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE);


    /**
     * Acepta la propuesta. Valida que {@code usuario} sea el destinatario
     * y que la propuesta esté en estado ACEPTADO.
     */
    public void aceptar(String perfilId) {
        validarUsuarioDestino(perfilId);
        validarAceptable();
        ejecutarIntercambio();
        EstadoPropuesta actual = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.ACEPTADO);
        estado.add(actual);
        setEstadoActual(actual);
    }

    /**
     * Se selecciona la propuesta. Valida que {@code usuario} sea el destinatario
     * y que la propuesta esté en estado SELECCIONADO. (En una subasta)
     */
    public void seleccionar(String perfilId) {
        validarUsuarioDestino(perfilId);
        validarPendiente();
        EstadoPropuesta actual = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.SELECCIONADO);
        estado.add(actual);
        setEstadoActual(actual);
    }

    /**
     * Rechaza la propuesta. Valida que {@code usuario} sea el destinatario
     * y que la propuesta esté en estado RECHAZADO.
     */
    public void rechazar(String perfilId) {
        validarUsuarioDestino(perfilId);
        validarPendiente();
        ejecutarRechazo();
        EstadoPropuesta actual = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.RECHAZADO);
        estado.add(actual);
        setEstadoActual(actual);
    }

    /**
     * Cancela la propuesta. Valida que {@code usuario} sea el autor
     * y que la propuesta esté en estado PENDIENTE.
     */
    public void cancelar(String perfilId) {
        validarUsuarioAutor(perfilId);
        validarPendiente();
        ejecutarRechazo();
        EstadoPropuesta actual = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.CANCELADO);
        estado.add(actual);
        setEstadoActual(actual);
    }

    private void ejecutarIntercambio() {
        this.getFiguritasOfrecidas()
            .forEach(f -> this.destinatario.getColeccion().eliminarFaltante(f));
        this.destinatario.getColeccion()
            .descontarRepetida(this.getFiguritaBuscada());

        this.getFiguritasOfrecidas()
            .forEach(f -> this.autor.getColeccion().descontarRepetida(f));
        this.autor.getColeccion().eliminarFaltante(this.getFiguritaBuscada());
    }

    private void ejecutarRechazo() {
        this.autor.getColeccion().sacarReservasRepetidas(this.getFiguritasOfrecidas());
    }

    private void validarPendiente() {
        if (getEstadoActual().getValor() != EstadoProceso.PENDIENTE) {
            throw new BadRequestException("La propuesta ya fue respondida");
        }
    }

    private void validarAceptable() {
        EstadoProceso estado = getEstadoActual().getValor();

        if (
            estado != EstadoProceso.PENDIENTE &&
                estado != EstadoProceso.SELECCIONADO
        ) {
            throw new BadRequestException(
                "La propuesta ya fue respondida"
            );
        }
    }

    private void validarUsuarioDestino(String perfilId) {
        if (!this.destinatario.getId().equals(perfilId)) {
            throw new BadRequestException("Solo el destinatario puede responder la propuesta");
        }
    }

    private void validarUsuarioAutor(String perfilId) {
        if (!this.autor.getId().equals(perfilId)) {
            throw new BadRequestException("Solo el destinatario puede responder la propuesta");
        }
    }

}
