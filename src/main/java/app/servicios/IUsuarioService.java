package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.dto.OperacionesDto;
import app.model.entities.Sugerencia;
import app.model.notificador.Notificacion;
import java.util.List;

public interface IUsuarioService {
    OperacionesDto getOperacionesUsuario(String userId);

    List<FiguritaIntercambiableDto> getIntercambiablesUsuario(String userId);
    Number agregarCalificacion(Integer calificacion, String userId);
    List<Sugerencia> getSugerencias(String userId);
    List<Notificacion> getNotificaciones(String userId);

}
