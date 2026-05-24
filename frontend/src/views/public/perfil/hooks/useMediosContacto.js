import { useState } from 'react'

export const useMediosContacto = (mediosEditando, setMediosEditando) => {
  const [indiceMedioEditando, setIndiceMedioEditando] = useState(-1)
  const [medioEditandoData, setMedioEditandoData] = useState({
    medio_comunicacion: 'TELEGRAM',
    valor: '',
  })
  const [nuevoMedioTipo, setNuevoMedioTipo] = useState('TELEGRAM')
  const [nuevoMedioValor, setNuevoMedioValor] = useState('')

  const agregarMedio = () => {
    if (!nuevoMedioValor.trim()) return
    setMediosEditando((prev) => [
      ...prev,
      { medio_comunicacion: nuevoMedioTipo, valor: nuevoMedioValor },
    ])
    setNuevoMedioValor('')
  }

  const eliminarMedio = (i) => {
    setMediosEditando((prev) => prev.filter((_, idx) => idx !== i))
    if (indiceMedioEditando === i) setIndiceMedioEditando(-1)
  }

  const confirmarEdicionMedio = (i) => {
    setMediosEditando((prev) => prev.map((m, idx) => (idx === i ? { ...medioEditandoData } : m)))
    setIndiceMedioEditando(-1)
  }

  const iniciarEdicionMedio = (i, medio) => {
    setIndiceMedioEditando(i)
    setMedioEditandoData({ ...medio })
  }

  const cancelarEdicionMedio = () => setIndiceMedioEditando(-1)

  return {
    indiceMedioEditando,
    medioEditandoData,
    setMedioEditandoData,
    nuevoMedioTipo,
    setNuevoMedioTipo,
    nuevoMedioValor,
    setNuevoMedioValor,
    agregarMedio,
    eliminarMedio,
    confirmarEdicionMedio,
    iniciarEdicionMedio,
    cancelarEdicionMedio,
  }
}
