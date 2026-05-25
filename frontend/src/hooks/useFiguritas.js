import { useEffect, useState } from 'react'
import { explorarFiguritas } from '@/services/explorarService'
import { useError } from '@/contexts/errorContext.jsx'

const useFiguritas = (q, jugador, seleccion, numero, tipos, page) => {
  const [figuritas, setFiguritas] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(false)
  const { handleError } = useError()

  useEffect(() => {
    setLoading(true)
    setError(false)
    const cargar = async () => {
      try {
        const data = await explorarFiguritas({ q, jugador, seleccion, numero, tipos, page })
        setFiguritas(data.contenido)
        setTotalPages(data.totalPages)
        setTotalElements(data.totalElements)
      } catch (error) {
        handleError(error, () => setError(true))
      } finally {
        setLoading(false)
      }
    }

    cargar()
  }, [q, jugador, seleccion, numero, tipos, page])

  return { figuritas, totalPages, totalElements, loading, error }
}

export default useFiguritas
