import { useState } from 'react'

export const TIPOS = [
  { key: 'intercambio', label: 'Intercambio' },
  { key: 'subasta', label: 'Subasta' },
]

const ESTADO_INICIAL = { tipos: [], jugador: '', seleccion: '', numero: '' }

const labelDeTipo = (key) => TIPOS.find((t) => t.key === key)?.label ?? key

const buildChipsActivos = ({ tipos, jugador, seleccion, numero }) => {
  const chips = []
  tipos.forEach((t) => chips.push({ campo: 'tipos', valor: t, label: labelDeTipo(t) }))
  if (jugador) chips.push({ campo: 'jugador', valor: jugador, label: jugador })
  if (seleccion) chips.push({ campo: 'seleccion', valor: seleccion, label: seleccion })
  if (numero) chips.push({ campo: 'numero', valor: numero, label: `#${numero}` })
  return chips
}

const useExplorarFiltros = (onAplicar) => {
  const [filtros, setFiltros] = useState(ESTADO_INICIAL)

  const actualizar = (cambios) => setFiltros((prev) => ({ ...prev, ...cambios }))

  const toggleTipo = (key) => {
    const yaSeleccionado = filtros.tipos.includes(key)
    const nuevosTipos = yaSeleccionado
      ? filtros.tipos.filter((t) => t !== key)
      : [...filtros.tipos, key]
    actualizar({ tipos: nuevosTipos })
  }

  const aplicar = () => onAplicar(filtros)

  const quitarFiltro = (campo, valor) => {
    const nuevos =
      campo === 'tipos'
        ? { ...filtros, tipos: filtros.tipos.filter((t) => t !== valor) }
        : { ...filtros, [campo]: '' }
    setFiltros(nuevos)
    onAplicar(nuevos)
  }

  return {
    ...filtros,
    chipsActivos: buildChipsActivos(filtros),
    toggleTipo,
    setJugador: (v) => actualizar({ jugador: v }),
    setSeleccion: (v) => actualizar({ seleccion: v }),
    setNumero: (v) => actualizar({ numero: v }),
    aplicar,
    quitarFiltro,
  }
}

export default useExplorarFiltros
