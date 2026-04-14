package app.servicios;

import app.dto.PropuestaDto;
import app.dto.request.CrearPropuestaRequest;
import app.model.entities.Propuesta;

public interface IPropuestaService {
    public PropuestaDto crearPropuesta(CrearPropuestaRequest request);
    public void aceptar(String id);
    public void rechazar(String id);
}
