import { api, handleAxiosError } from './api.js'

const TIPO_MAP = {
  intercambio: 'INTERCAMBIO',
  subasta: 'SUBASTA',
}

const buildParams = ({ q, jugador, seleccion, numero, tipo, page, size }) => {
  const tipoParam = tipo && tipo !== 'todos' ? TIPO_MAP[tipo] : undefined
  if (q) {
    return { q, tipo: tipoParam, pagina: page, tamanioPagina: size }
  }
  return {
    jugador: jugador || undefined,
    seleccion: seleccion ? seleccion.toUpperCase() : undefined,
    numero: numero || undefined,
    tipo: tipoParam,
    pagina: page,
    tamanioPagina: size,
  }
}

export const explorarFiguritas = async ({
  q,
  jugador,
  seleccion,
  numero,
  tipo,
  page = 0,
  size = 12,
} = {}) => {
  try {
    const { data } = await api.get('/figuritas/intercambiables', {
      params: buildParams({ q, jugador, seleccion, numero, tipo, page, size }),
    })
    return {
      contenido: data.contenido,
      totalElements: data.cantidad_de_elementos,
      totalPages: data.cantidad_de_paginas,
    }
  } catch (error) {
    handleAxiosError(error)
  }
}
