package app.servicios;

import app.dto.CalificacionDto;
import app.dto.ContadorDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.NotificacionesDto;
import app.dto.OperacionesDto;
import app.dto.SugerenciaDto;
import app.dto.filtros.SugerenciasFiltro;
import app.model.entities.Calificacion;

import java.util.List;

public interface IPerfilService {

    /**
     * Retorna el resumen de operaciones del perfil: figuritas publicadas,
     * propuestas enviadas, propuestas recibidas y subastas activas.
     * Retorna {@code null} si el perfil no existe.
     */
    OperacionesDto obtenerOperacionesPerfil(String userId);

    /**
     * Retorna las figuritas intercambiables del perfil.
     * Lanza {@link app.exceptions.NotFoundException} si el perfil no existe.
     */
    List<FiguritaIntercambiableDto> obtenerIntercambiablesPerfil(String userId);

    /**
     * Agrega una calificación de {@code autor} al perfil destino y retorna el nuevo promedio.
     * Valida que el valor esté entre 1 y 5 inclusive.
     */
    CalificacionDto agregarCalificacion(String autorId, String perfilDestinoId, Integer valor,
                                        String descripcion);

    /**
     * Sugiere perfiles que tienen repetidas figuritas que le faltan al usuario.
     * Recorre todos los perfiles y cruza sus repetidas contra los faltantes del usuario objetivo.
     */
    List<SugerenciaDto> obtenerSugerencias(String userId, SugerenciasFiltro filtro);

    /**
     * Brinda estadisticas simples del perfil en cuestion.
     * Las estadisticas son cantidad de repetidas y cantidad de faltantes.
     */
    List<ContadorDto> obtenerContadores(String userId);

    List<NotificacionesDto> obtenerNotificaciones(String userId);
}
