package app.servicios;

import app.dto.OperacionesDto;
import app.model.entities.Sugerencia;
import app.model.notificador.Notificacion;
import java.util.List;

public interface UsuarioService {
    OperacionesDto getOperacionesUsuario(String userId);
    Number agregarCalificacion(Integer calificacion, String userId);
    List<Sugerencia> getSugerencias(String userId);
    List<Notificacion> getNotificaciones(String userId);
}
