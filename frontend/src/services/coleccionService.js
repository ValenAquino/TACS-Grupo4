import { api, handleAxiosError } from './api.js'

const COLECCIONES_URL = '/colecciones'

// export const buscarFaltantes = async (filtros) => {
//   try {
//     const { data } = await api.get(`${COLECCIONES_URL}/faltantes`, { params: filtros })
//     return data
//   } catch (error) {
//     handleAxiosError(error)
//   }
// }

// export const buscarRepetidas = async (filtros) => {
//   try {
//     const { coleccion } = await api.get(`${COLECCIONES_URL}/repetidas`, { params: filtros })

//     return coleccion
//   } catch (error) {
//     handleAxiosError(error)
//   }
// }

export const buscarFaltantes = async (filtros) => {
  const { jugador = '', pagina = 1, limite = 10 } = filtros ?? {}
  const todas = [
    { id: 'ARG-1', numero: 1, jugador: 'Emiliano Martínez', seleccion: 'Argentina' },
    { id: 'ARG-2', numero: 2, jugador: 'Nahuel Molina', seleccion: 'Argentina' },
    { id: 'ARG-3', numero: 3, jugador: 'Cristian Romero', seleccion: 'Argentina' },
    { id: 'BRA-1', numero: 1, jugador: 'Alisson', seleccion: 'Brasil' },
    { id: 'BRA-2', numero: 2, jugador: 'Danilo', seleccion: 'Brasil' },
    { id: 'FRA-1', numero: 1, jugador: 'Hugo Lloris', seleccion: 'Francia' },
    { id: 'FRA-2', numero: 2, jugador: 'Kylian Mbappé', seleccion: 'Francia' },
  ]
  const filtradas = jugador
    ? todas.filter((f) => f.jugador.toLowerCase().includes(jugador.toLowerCase()))
    : todas
  const inicio = (pagina - 1) * limite
  const contenido = filtradas.slice(inicio, inicio + limite)
  await new Promise((res) => setTimeout(res, 400))
  return {
    contenido,
    cantidad_de_elementos: filtradas.length,
    cantidad_de_paginas: Math.ceil(filtradas.length / limite),
    numero: pagina,
  }
}

export const buscarRepetidas = async (filtros) => {
  const { jugador = '', pagina = 1, limite = 10 } = filtros ?? {}
  const todas = [
    {
      figurita_id: 'fig-1',
      numero: 10,
      jugador: 'Lionel Messi',
      seleccion: 'Argentina',
      cantidad_existente: 3,
      cantidad_reservada: 1,
      metodos: ['SUBASTA', 'INTERCAMBIO'],
      usuario_id: 'u-1',
      nombre_usuario: 'Juan',
      reputacion: 4,
      posicion: 'Delantero',
    },
    {
      figurita_id: 'fig-2',
      numero: 11,
      jugador: 'Ángel Di María',
      seleccion: 'Argentina',
      cantidad_existente: 2,
      cantidad_reservada: 0,
      metodos: ['INTERCAMBIO'],
      usuario_id: 'u-1',
      nombre_usuario: 'Juan',
      reputacion: 4,
      posicion: 'Extremo',
    },
    {
      figurita_id: 'fig-3',
      numero: 1,
      jugador: 'Emiliano Martínez',
      seleccion: 'Argentina',
      cantidad_existente: 1,
      cantidad_reservada: 1,
      metodos: ['SUBASTA'],
      usuario_id: 'u-1',
      nombre_usuario: 'Juan',
      reputacion: 4,
      posicion: 'Arquero',
    },
    {
      figurita_id: 'fig-4',
      numero: 5,
      jugador: 'Alexis Mac Allister',
      seleccion: 'Argentina',
      cantidad_existente: 4,
      cantidad_reservada: 0,
      metodos: ['SUBASTA', 'INTERCAMBIO'],
      usuario_id: 'u-1',
      nombre_usuario: 'Juan',
      reputacion: 4,
      posicion: 'Mediocampista',
    },
    {
      figurita_id: 'fig-5',
      numero: 22,
      jugador: 'Lautaro Martínez',
      seleccion: 'Argentina',
      cantidad_existente: 2,
      cantidad_reservada: 0,
      metodos: ['INTERCAMBIO'],
      usuario_id: 'u-1',
      nombre_usuario: 'Juan',
      reputacion: 4,
      posicion: 'Delantero',
    },
  ]
  const filtradas = jugador
    ? todas.filter((f) => f.jugador.toLowerCase().includes(jugador.toLowerCase()))
    : todas
  const inicio = (pagina - 1) * limite
  const contenido = filtradas.slice(inicio, inicio + limite)
  await new Promise((res) => setTimeout(res, 400))
  return {
    publicadas: todas.length,
    disponibles: todas.filter((f) => f.cantidad_existente > f.cantidad_reservada).length,
    contenido,
    cantidad_de_elementos: filtradas.length,
    cantidad_de_paginas: Math.ceil(filtradas.length / limite),
    numero: pagina,
  }
}
export const agregarFaltante = async (colId, faltante) => {
  try {
    const { data } = await api.post(`${COLECCIONES_URL}/faltantes`, { fig_id: faltante.id })

    return data
  } catch (error) {
    handleAxiosError(error)
  }
}

export const agregarRepetida = async (repetida) => {
  try {
    const { data } = await api.post(`${COLECCIONES_URL}/repetidas`, {
      fig_id: repetida.id,
      cantidad_existente: repetida.cantidad,
      modos_intercambio: repetida.modosIntercambio,
    })

    return data
  } catch (error) {
    handleAxiosError(error)
  }
}
