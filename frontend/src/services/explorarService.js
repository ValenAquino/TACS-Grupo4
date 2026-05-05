import { api, handleAxiosError } from './api.js'

const TIPO_MAP = {
  ambos: 'SUBASTA_E_INTERCAMBIO',
  intercambio: 'INTERCAMBIO',
  subasta: 'SUBASTA',
}

const resolverTipo = (metodos) => {
  if (
    metodos.includes('SUBASTA_E_INTERCAMBIO') ||
    (metodos.includes('INTERCAMBIO') && metodos.includes('SUBASTA'))
  )
    return 'ambos'
  return metodos.includes('SUBASTA') ? 'subasta' : 'intercambio'
}

const mapFigurita = (f) => ({
  id: f.figurita_id,
  number: f.numero,
  name: f.jugador,
  subtitle: `${f.seleccion} · ${f.posicion}`,
  type: resolverTipo(f.metodos),
  available: f.cantidad_existente,
  user: f.nombre_usuario
    ? {
        initials: f.nombre_usuario.slice(0, 2).toUpperCase(),
        name: f.nombre_usuario,
        stars: f.reputacion ?? 0,
        color: '#6b7280',
      }
    : null,
})

const buildParams = ({ jugador, seleccion, numero, tipo, page, size, ordenar }) => ({
  jugador: jugador || undefined,
  seleccion: seleccion ? seleccion.toUpperCase() : undefined,
  numero: numero || undefined,
  tipo: tipo && tipo !== 'todos' ? TIPO_MAP[tipo] : undefined,
  pagina: page,
  tamanioPagina: size,
  ordenar,
})

export const explorarFiguritas = async ({
  jugador,
  seleccion,
  numero,
  tipo,
  page = 0,
  size = 12,
  ordenar,
} = {}) => {
  try {
    const { data } = await api.get('/figuritas', {
      params: buildParams({ jugador, seleccion, numero, tipo, page, size, ordenar }),
    })
    return {
      content: data.contenido.map(mapFigurita),
      totalElements: data.cantidad_de_elementos,
      totalPages: data.cantidad_de_paginas,
      number: page,
      size,
    }
  } catch (error) {
    handleAxiosError(error)
  }
}
