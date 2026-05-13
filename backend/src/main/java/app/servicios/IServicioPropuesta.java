package app.servicios;

import app.dto.PropuestaDto;
import app.dto.request.CrearPropuestaRequest;

public interface IServicioPropuesta {

    /**
     * Crea una propuesta de intercambio. Valida que el usuario origen,
     * destino y figuritas existan. El estado inicial es PENDIENTE.
     */
    PropuestaDto crearPropuesta(CrearPropuestaRequest request);

    /**
     * Acepta la propuesta. Resuelve el perfil a partir del {@code usuarioId}
     * y delega la validación de permisos y estado en {@link app.model.entities.Propuesta#aceptar}.
     */
    void aceptar(String id, String usuarioId);

    /**
     * Rechaza la propuesta. Resuelve el perfil a partir del {@code usuarioId}
     * y delega la validación de permisos y estado en {@link app.model.entities.Propuesta#rechazar}.
     */
    void rechazar(String id, String usuarioId);
}
