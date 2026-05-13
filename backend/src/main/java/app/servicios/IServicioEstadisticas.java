package app.servicios;

import app.dto.EstadisticasDto;

public interface IServicioEstadisticas {

    /**
     * Retorna estadísticas globales: total de usuarios, figuritas publicadas
     * (suma de repetidas de todas las colecciones), propuestas y subastas activas.
     */
    EstadisticasDto obtenerEstadisticas();
}
