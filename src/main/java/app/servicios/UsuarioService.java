package app.servicios;

import app.dto.OperacionesDto;
import app.model.notificador.Notificacion;

import java.util.List;

public interface UsuarioService {
    OperacionesDto getOperacionesUsuario(String userId);
    List<Notificacion> getNotificaciones(String userId);
}
