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
//     try {
//         const { coleccion } = await api.get(
//             `${COLECCIONES_URL}/repetidas`,
//             {params: filtros},
//         );

//         return coleccion;
//     } catch (error) {
//         handleAxiosError(error);
//     }
// };
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
    cantidadDeElementos: filtradas.length,
    cantidadDePaginas: Math.ceil(filtradas.length / limite),
    numero: limite,
  }
}
export const buscarRepetidas = async (filtros) => {
  const { jugador = '', pagina = 1, limite = 10 } = filtros ?? {}

  const todas = [
    {
      figuritaId: 'fig-1',
      numero: 10,
      jugador: 'Lionel Messi',
      seleccion: 'Argentina',
      cantidadExistente: 3,
      cantidadReservada: 1,
      metodos: ['SUBASTA', 'INTERCAMBIO'],
      usuarioId: 'u-1',
      nombreUsuario: 'Juan',
      reputacion: 4,
      posicion: 'Delantero',
    },
    {
      figuritaId: 'fig-2',
      numero: 11,
      jugador: 'Ángel Di María',
      seleccion: 'Argentina',
      cantidadExistente: 2,
      cantidadReservada: 0,
      metodos: ['INTERCAMBIO'],
      usuarioId: 'u-1',
      nombreUsuario: 'Juan',
      reputacion: 4,
      posicion: 'Extremo',
    },
    {
      figuritaId: 'fig-3',
      numero: 1,
      jugador: 'Emiliano Martínez',
      seleccion: 'Argentina',
      cantidadExistente: 1,
      cantidadReservada: 1,
      metodos: ['SUBASTA'],
      usuarioId: 'u-1',
      nombreUsuario: 'Juan',
      reputacion: 4,
      posicion: 'Arquero',
    },
    {
      figuritaId: 'fig-4',
      numero: 5,
      jugador: 'Alexis Mac Allister',
      seleccion: 'Argentina',
      cantidadExistente: 4,
      cantidadReservada: 0,
      metodos: ['SUBASTA', 'INTERCAMBIO'],
      usuarioId: 'u-1',
      nombreUsuario: 'Juan',
      reputacion: 4,
      posicion: 'Mediocampista',
    },
    {
      figuritaId: 'fig-5',
      numero: 22,
      jugador: 'Lautaro Martínez',
      seleccion: 'Argentina',
      cantidadExistente: 2,
      cantidadReservada: 0,
      metodos: ['INTERCAMBIO'],
      usuarioId: 'u-1',
      nombreUsuario: 'Juan',
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
    disponibles: todas.filter((f) => f.cantidadExistente > f.cantidadReservada).length,
    contenido,
    cantidadDeElementos: filtradas.length,
    cantidadDePaginas: Math.ceil(filtradas.length / limite),
    numero: pagina - 1,
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
