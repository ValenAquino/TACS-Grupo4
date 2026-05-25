import { useState } from 'react'

const TIPOS = [
  { key: 'intercambio', label: 'Intercambio' },
  { key: 'subasta', label: 'Subasta' },
]

const useExplorarFiltros = (onAplicar) => {
  const [tipos, setTipos] = useState([])
  const [jugador, setJugador] = useState('')
  const [seleccion, setSeleccion] = useState('')
  const [numero, setNumero] = useState('')

  const toggleTipo = (key) => {
    setTipos((prev) => (prev.includes(key) ? prev.filter((t) => t !== key) : [...prev, key]))
  }

  const aplicar = () => {
    onAplicar({ tipos, jugador, seleccion, numero })
  }

  const quitarFiltro = (campo, valor) => {
    const nuevosTipos = campo === 'tipo' ? tipos.filter((t) => t !== valor) : tipos
    const nuevoJugador = campo === 'jugador' ? '' : jugador
    const nuevaSeleccion = campo === 'seleccion' ? '' : seleccion
    const nuevoNumero = campo === 'numero' ? '' : numero

    setTipos(nuevosTipos)
    if (campo === 'jugador') setJugador('')
    if (campo === 'seleccion') setSeleccion('')
    if (campo === 'numero') setNumero('')

    onAplicar({ tipos: nuevosTipos, jugador: nuevoJugador, seleccion: nuevaSeleccion, numero: nuevoNumero })
  }

  const chipsActivos = [
    ...tipos.map((t) => ({ campo: 'tipo', valor: t, label: TIPOS.find((x) => x.key === t)?.label ?? t })),
    ...(jugador ? [{ campo: 'jugador', valor: jugador, label: jugador }] : []),
    ...(seleccion ? [{ campo: 'seleccion', valor: seleccion, label: seleccion }] : []),
    ...(numero ? [{ campo: 'numero', valor: numero, label: `#${numero}` }] : []),
  ]

  return {
    tipos,
    jugador,
    seleccion,
    numero,
    chipsActivos,
    toggleTipo,
    setJugador,
    setSeleccion,
    setNumero,
    aplicar,
    quitarFiltro,
  }
}

export { TIPOS }
export default useExplorarFiltros
