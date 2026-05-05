import { useEffect, useState } from 'react'
import { obtenerEstadisticas } from '@/services/administradorService.js'

const useEstadisticasAdmin = () => {
  const [stats, setStats] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    obtenerEstadisticas()
      .then(setStats)
      .catch((err) => setError(err?.message ?? 'Error al cargar estadísticas'))
      .finally(() => setCargando(false))
  }, [])

  return { stats, cargando, error }
}

export default useEstadisticasAdmin
