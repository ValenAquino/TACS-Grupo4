import { useEffect, useState } from 'react'
import { buscarCalificaciones } from '@/services/perfilService.js'
import { useError } from '@/contexts/errorContext.jsx'
import usePaginacion from '@/hooks/usePaginacion.js'

export const useCalificaciones = () => {
  const [reviews, setReviews] = useState({})
  const [loading, setLoading] = useState(false)
  const [filtros] = useState({})
  const { pagina, setPagina } = usePaginacion()

  const { handleError } = useError()

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true)
        const data = await buscarCalificaciones({ ...filtros, pagina, limite: 10 })
        setReviews(data)
      } catch (error) {
        handleError(error, () => {})
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [filtros, handleError, pagina])

  return { reviews, loading, pagina, setPagina }
}
