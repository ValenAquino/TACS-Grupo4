import { useEffect, useState } from 'react'
import { obtenerEstadisticas } from '@/services/administradorService.js'

const formatDate = (date) => date.toISOString().split('T')[0]

const getDefaultRange = () => {
  const hasta = new Date()
  const desde = new Date()
  desde.setDate(desde.getDate() - 6)
  return { desde: formatDate(desde), hasta: formatDate(hasta) }
}

const useEstadisticasAdmin = () => {
  const defaultRange = getDefaultRange()
  const [desde, setDesde] = useState(defaultRange.desde)
  const [hasta, setHasta] = useState(defaultRange.hasta)
  const [stats, setStats] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!desde || !hasta || desde > hasta) return

    setCargando(true)
    setError(null)
    obtenerEstadisticas(desde, hasta)
      .then(setStats)
      .catch((err) => setError(err?.message ?? 'Error al cargar estadísticas'))
      .finally(() => setCargando(false))
  }, [desde, hasta])

  return { stats, cargando, error, desde, setDesde, hasta, setHasta }
}

export default useEstadisticasAdmin
