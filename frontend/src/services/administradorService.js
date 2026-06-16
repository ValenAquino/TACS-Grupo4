import { api, handleAxiosError } from './api.js'

export const obtenerEstadisticas = async (desde, hasta) => {
  try {
    const { data } = await api.get('/administrador/estadisticas', {
      params: { desde, hasta },
    })
    return {
      totalUsuarios: data.total_usuarios,
      totalFiguritasPublicadas: data.total_figuritas_publicadas,
      totalPropuestas: data.total_propuestas,
      totalSubastasActivas: data.total_subastas_activas,
      propuestasPorEstado: data.propuestas_por_estado,
      figuritasPorModalidad: data.figuritas_por_modalidad
        ? {
            soloIntercambio: data.figuritas_por_modalidad.solo_intercambio,
            soloSubasta: data.figuritas_por_modalidad.solo_subasta,
            ambos: data.figuritas_por_modalidad.ambos,
          }
        : undefined,
    }
  } catch (error) {
    handleAxiosError(error)
  }
}
