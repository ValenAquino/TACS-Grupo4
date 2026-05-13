package app.servicios;

import app.dto.ContadorDto;
import app.dto.FiguritaIntercambiableDto;
import app.dto.NotificacionesDto;
import app.dto.OperacionesDto;
import app.dto.PerfilDto;
import app.dto.SugerenciaPaginadaDto;
import app.dto.filtros.SugerenciasFiltro;
import app.dto.request.PerfilRequest;

import app.model.entities.MetodoIntercambio;
import java.util.List;

public interface IServicioPerfil {

    PerfilDto crearPerfil(PerfilRequest perfil);

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
     * Agrega una calificación de {@code autor} al perfil destino.
     * Valida que el valor esté entre 1 y 5 inclusive.
     */
    void agregarCalificacion(String autorId, String perfilDestinoId, Integer valor, String descripcion, String transactionId, MetodoIntercambio tipoTransaccion);


    /**
     * Sugiere perfiles que tienen repetidas figuritas que le faltan al usuario.
     * Recorre todos los perfiles y cruza sus repetidas contra los faltantes del usuario objetivo.
     */
    SugerenciaPaginadaDto obtenerSugerencias(String userId, SugerenciasFiltro filtro);

    /**
     * Brinda estadisticas simples del perfil en cuestion.
     * Las estadisticas son cantidad de repetidas y cantidad de faltantes.
     */
    List<ContadorDto> obtenerContadores(String userId);

    List<NotificacionesDto> obtenerNotificaciones(String userId);

    /**
     * Retorna las figuritas faltantes de la colección del perfil.
     * Lanza {@link app.exceptions.NotFoundException} si el perfil no existe.
     *
     * @param userId  id del usuario cuyas faltantes se quieren obtener
     * @return        lista de {@link app.dto.FiguritaDto} correspondientes a las faltantes
     */
    List<app.dto.FiguritaDto> obtenerFaltantes(String userId);

    /**
     * Retorna las figuritas repetidas de la colección del perfil.
     * Lanza {@link app.exceptions.NotFoundException} si el perfil no existe.
     *
     * @param userId  id del usuario cuyas repetidas se quieren obtener
     * @return        lista de {@link FiguritaIntercambiableDto} correspondientes a las repetidas
     */
    List<FiguritaIntercambiableDto> obtenerRepetidas(String userId);

    /**
     * Obtiene los datos básicos del perfil del usuario indicado,
     * incluyendo nombre, iniciales y calificación promedio.
     */
    PerfilDto obtenerPerfil(String userId);
}
