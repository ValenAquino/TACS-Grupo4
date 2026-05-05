import { api, handleAxiosError } from './api.js'

const EMOJIS = ['⚽', '🏆', '🥇', '⭐', '🌟', '🦁', '🦅', '🔥', '💫', '🎯', '🏅', '⚡']
const EMOJI_BGS = [
  '#dbeafe',
  '#d1fae5',
  '#ede9fe',
  '#ffedd5',
  '#fce7f3',
  '#fef9c3',
  '#ecfdf5',
  '#fee2e2',
]

const randomEmoji = () => EMOJIS[Math.floor(Math.random() * EMOJIS.length)]
const randomBg = () => EMOJI_BGS[Math.floor(Math.random() * EMOJI_BGS.length)]

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
  imageUrl: f.str_cutout,
  emoji: randomEmoji(),
  emojiBg: randomBg(),
  user: f.nombre_usuario
    ? {
        initials: f.nombre_usuario.slice(0, 2).toUpperCase(),
        name: f.nombre_usuario,
        stars: f.reputacion ?? 0,
        color: '#6b7280',
      }
    : null,
})

const buildParams = ({ q, jugador, seleccion, numero, tipo, page, size }) => {
  const tipoParam = tipo && tipo !== 'todos' ? TIPO_MAP[tipo] : undefined
  if (q) {
    return {
      q: q,
      tipo: tipoParam,
      pagina: page,
      tamanioPagina: size,
    }
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
    const { data } = await api.get('/figuritas', {
      params: buildParams({ q, jugador, seleccion, numero, tipo, page, size }),
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
