import { useState } from 'react'

const validarMedio = (tipo, valor) => {
  if (!valor.trim()) return 'El valor no puede estar vacío'
  if (tipo === 'MAIL') {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor)) return 'Email inválido'
  }
  if (tipo === 'TELEGRAM') {
    if (!/^@[a-zA-Z0-9_]{2,}$/.test(valor)) return 'Debe tener formato @usuario'
  }
  return null
}

export const useMediosContacto = (mediosEditando, setMediosEditando) => {
  const [indiceMedioEditando, setIndiceMedioEditando] = useState(-1)
  const [medioEditandoData, setMedioEditandoData] = useState({
    medio_comunicacion: 'TELEGRAM',
    valor: '',
  })
  const [nuevoMedioTipo, setNuevoMedioTipo] = useState('TELEGRAM')
  const [nuevoMedioValor, setNuevoMedioValor] = useState('')
  const [errorNuevoMedio, setErrorNuevoMedio] = useState(null)
  const [errorMedioEditando, setErrorMedioEditando] = useState(null)

  const agregarMedio = () => {
    const error = validarMedio(nuevoMedioTipo, nuevoMedioValor)
    if (error) {
      setErrorNuevoMedio(error)
      return
    }
    setMediosEditando((prev) => [
      ...prev,
      { medio_comunicacion: nuevoMedioTipo, valor: nuevoMedioValor },
    ])
    setNuevoMedioValor('')
    setErrorNuevoMedio(null)
  }

  const eliminarMedio = (i) => {
    setMediosEditando((prev) => prev.filter((_, idx) => idx !== i))
    if (indiceMedioEditando === i) setIndiceMedioEditando(-1)
  }

  const confirmarEdicionMedio = (i) => {
    const error = validarMedio(medioEditandoData.medio_comunicacion, medioEditandoData.valor)
    if (error) {
      setErrorMedioEditando(error)
      return
    }
    setMediosEditando((prev) => prev.map((m, idx) => (idx === i ? { ...medioEditandoData } : m)))
    setIndiceMedioEditando(-1)
    setErrorMedioEditando(null)
  }

  const iniciarEdicionMedio = (i, medio) => {
    setIndiceMedioEditando(i)
    setMedioEditandoData({ ...medio })
    setErrorMedioEditando(null)
  }

  const cancelarEdicionMedio = () => {
    setIndiceMedioEditando(-1)
    setErrorMedioEditando(null)
  }

  return {
    indiceMedioEditando,
    medioEditandoData,
    setMedioEditandoData: (updater) => {
      setErrorMedioEditando(null)
      setMedioEditandoData(updater)
    },
    nuevoMedioTipo,
    setNuevoMedioTipo: (v) => {
      setNuevoMedioTipo(v)
      setErrorNuevoMedio(null)
    },
    nuevoMedioValor,
    setNuevoMedioValor: (v) => {
      setNuevoMedioValor(v)
      setErrorNuevoMedio(null)
    },
    errorNuevoMedio,
    errorMedioEditando,
    agregarMedio,
    eliminarMedio,
    confirmarEdicionMedio,
    iniciarEdicionMedio,
    cancelarEdicionMedio,
  }
}
