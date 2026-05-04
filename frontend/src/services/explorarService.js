import { api, handleAxiosError } from './api.js';

const MOCK_FIGURITAS = [
  {
    id: 'ARG-14',
    number: 14,
    type: 'intercambio',
    emoji: '🧤',
    name: 'Dibu Martínez',
    subtitle: 'Argentina · Arquero',
    available: 3,
    emojiBg: '#dbeafe',
    user: { initials: 'RL', name: 'rodrigo_l', stars: 2, color: '#ef4444' },
  },
  {
    id: 'BRA-7',
    number: 7,
    type: 'subasta',
    emoji: '⚽',
    name: 'Vinicius Jr.',
    subtitle: 'Brasil · Delantero',
    extra: 'Subasta · 2h 14m',
    emojiBg: '#d1fae5',
    user: { initials: 'PF', name: 'pedro_f', stars: 4, color: '#8b5cf6' },
  },
  {
    id: 'ARG-10',
    number: 10,
    type: 'ambos',
    emoji: '⭐',
    name: 'Messi',
    subtitle: 'Argentina · Delantero',
    available: 1,
    emojiBg: '#ede9fe',
    user: { initials: 'SV', name: 'sofi_v', stars: 5, color: '#6366f1' },
  },
  {
    id: 'EST-23',
    number: 23,
    type: 'intercambio',
    emoji: '🏟️',
    name: 'Lusail Stadium',
    subtitle: 'Estadios · Especial',
    available: 2,
    emojiBg: '#ffedd5',
    user: { initials: 'JM', name: 'juani_m', stars: 3, color: '#f59e0b' },
  },
  {
    id: 'FRA-88',
    number: 88,
    type: 'subasta',
    emoji: '✦',
    name: 'Mbappé Brillante',
    subtitle: 'Francia · Especial',
    extra: 'Subasta · 45m',
    emojiBg: '#fce7f3',
    user: { initials: 'CR', name: 'caro_r', stars: 5, color: '#ec4899' },
  },
  {
    id: 'ESP-55',
    number: 55,
    type: 'intercambio',
    emoji: '🦅',
    name: 'Escudo España',
    subtitle: 'España · Escudo',
    available: 5,
    emojiBg: '#ecfdf5',
    user: { initials: 'KL', name: 'kari_l', stars: 4, color: '#06b6d4' },
  },
];

const filtrarMock = ({ jugador, seleccion, numero, tipo }) =>
  MOCK_FIGURITAS.filter((f) => {
    if (jugador && !f.name.toLowerCase().includes(jugador.toLowerCase())) return false
    if (seleccion && !f.subtitle.toLowerCase().includes(seleccion.toLowerCase())) return false
    if (numero && f.number !== parseInt(numero)) return false
    if (tipo && tipo !== 'todos' && f.type !== tipo) return false
    return true
  })

const paginarMock = (items, page, size) => {
  const start = page * size
  return {
    content: items.slice(start, start + size),
    totalElements: items.length,
    totalPages: Math.ceil(items.length / size),
    number: page,
    size,
  }
}

const ordenarMock = (items, ordenar) => {
  const copia = [...items]
  if (ordenar === 'numero') return copia.sort((a, b) => a.number - b.number)
  if (ordenar === 'reputacion') return copia.sort((a, b) => (b.user?.stars ?? 0) - (a.user?.stars ?? 0))
  return copia.sort((a, b) => b.id.localeCompare(a.id))
}

export const explorarFiguritas = async ({ jugador, seleccion, numero, tipo, page = 0, size = 4, ordenar = 'recientes' } = {}) => {
  try {
    const { data } = await api.get('/figuritas', { params: { jugador, seleccion, numero, tipo, page, size, ordenar } })
    return data
  } catch {
    // TODO: eliminar fallback cuando se integre el backend
    const filtradas = filtrarMock({ jugador, seleccion, numero, tipo })
    const ordenadas = ordenarMock(filtradas, ordenar)
    return paginarMock(ordenadas, page, size)
  }
}
