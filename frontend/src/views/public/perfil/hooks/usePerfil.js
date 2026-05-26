import { useEffect, useState } from 'react'
import { buscarContadores, buscarPerfil } from '@/services/perfilService.js'
import { useAuth } from '@/contexts/userContext.jsx'
import { useError } from '@/contexts/errorContext.jsx'
import { useToast } from '@/contexts/toastContext.jsx'
import { truncarADosDecimales } from '@/utils/estandarizar.js'

export const usePerfil = () => {
  const [perfil, setPerfil] = useState({})
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState([])

  const { handleError } = useError()
  const { showToast } = useToast()
  const { user, cerrarSesion } = useAuth()

  const manejarCierreDeSesion = async () => {
    try {
      await cerrarSesion()
      showToast('Cierre de sesion exitoso', 'success')
    } catch (error) {
      handleError(error, () => {})
    }
  }

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true)
        const perfilData = await buscarPerfil()
        const statsData = await buscarContadores()
        setPerfil(perfilData)
        setStats(statsData)
      } catch (error) {
        handleError(error, () => {})
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [])

  return {
    perfil,
    setPerfil,
    loading,
    stats,
    promedio: truncarADosDecimales(perfil.calificacion_media),
    perfilId: user.perfil_id,
    manejarCierreDeSesion,
  }
}
