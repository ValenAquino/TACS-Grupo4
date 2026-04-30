package app.model.entities;

import app.exceptions.PropuestaException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Propuesta {
    private String id;
    private Perfil autor;
    private Perfil destinatario;
    private List<Figurita> figuritasOfrecidas;
    private Figurita figuritaBuscada;
    private List<EstadoPropuesta> estado;

    public Propuesta(String id, Perfil autor, Perfil destinatario, List<Figurita> figuritasOfrecidas,
                     Figurita figuritaBuscada) {
        this.id = id;
        this.autor = autor;
        this.destinatario = destinatario;
        this.figuritasOfrecidas = figuritasOfrecidas;
        this.figuritaBuscada = figuritaBuscada;
        this.estado = new ArrayList<>(
            List.of(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE))
        );    }


    //Valído que no este pendiente y que solo lo pueda aceptar el usuario Correspondiente.
    //Chequear si eso está bien o no es necesario.

    /**
     * Retorna el estado más reciente de la propuesta. Si la lista está vacía
     * (puede ocurrir al deserializar), inicializa con PENDIENTE.
     */
    public EstadoPropuesta obtenerEstadoActual() {
        if (estado == null || estado.isEmpty()) {
            EstadoPropuesta inicial = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.PENDIENTE);
            estado = new ArrayList<>();
            estado.add(inicial);
            return inicial;
        }
        return estado.get(estado.size() - 1);
    }

    /**
     * Acepta la propuesta. Valida que {@code usuario} sea el destinatario
     * y que la propuesta esté en estado PENDIENTE.
     */
    public void aceptar(Perfil usuario) {
        validarUsuarioDestino(usuario);
        validarPendiente();
        estado.add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.ACEPTADO));
    }

    /**
     * Rechaza la propuesta. Valida que {@code usuario} sea el destinatario
     * y que la propuesta esté en estado PENDIENTE.
     */
    public void rechazar(Perfil usuario) {
        validarUsuarioDestino(usuario);
        validarPendiente();
        estado.add(new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.RECHAZADO));
    }

    private void validarPendiente() {
        if (obtenerEstadoActual().getValor() != EstadoProceso.PENDIENTE) {
            throw new PropuestaException("La propuesta ya fue respondida");
        }
    }

    private void validarUsuarioDestino(Perfil usuario) {
        if (!this.destinatario.getId().equals(usuario.getId())) {
            throw new PropuestaException("Solo el destinatario puede responder la propuesta");
        }
    }
}
