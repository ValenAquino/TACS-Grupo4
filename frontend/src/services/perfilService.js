import { api, handleAxiosError } from './api.js'

const PERFIL_URL = '/perfil'

export const buscarContadoresSugerencias = async () => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/contadores`, {})
    return data
  } catch (error) {
    handleAxiosError(error)
  }
}

export const buscarSugerencias = async ({ tipo, pagina, limite }) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/sugerencias`, {
      params: { tipo, paginaActual: pagina, limite },
    })
    return data
  } catch (error) {
    handleAxiosError(error)
  }
}

export const calificarPerfil = async (
  autorId,
  perfilId,
  { valor, descripcion, transactionId, tipoTransaccion },
) => {
  await api.post(`/perfil/calificaciones`, {
    destinatario_id: perfilId,
    valor,
    descripcion,
    transaction_id: transactionId,
    tipo_transaccion: tipoTransaccion,
  })
}

export const buscarPerfil = async () => {
  try {
    const { data } = await api.get(`${PERFIL_URL}`)
    return data
  } catch (error) {
    handleAxiosError(error)
  }
}

export const buscarCalificaciones = async (filtros) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/calificaciones`, { params: filtros })
    return data
  } catch (error) {
    handleAxiosError(error)
  }
}

export const buscarContadores = async () => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/contadores`)
    return data
  } catch (error) {
    handleAxiosError(error)
  }
}

export const editarPerfil = async ({ nombre }) => {
  try {
    await api.put(PERFIL_URL, { nombre })
  } catch (error) {
    handleAxiosError(error)
  }
}
