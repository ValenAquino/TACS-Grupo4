package app.servicios;

import app.dto.FiguritaIntercambiableDto;
import app.dto.NotificacionesDto;
import app.dto.OperacionesDto;
import app.dto.SugerenciaDto;
import java.util.List;

public interface IPerfilService {
    OperacionesDto obtenerOperacionesPerfil(String userId);

    List<FiguritaIntercambiableDto> obtenerIntercambiablesPerfil(String userId);
    Number agregarCalificacion(Integer calificacion, String userId);
    List<SugerenciaDto> obtenerSugerencias(String userId);
    List<NotificacionesDto> obtenerNotificaciones(String userId);
}
