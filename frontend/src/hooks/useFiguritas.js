import { useEffect, useState } from 'react'
import { explorarFiguritas } from '@/services/explorarService'

const useFiguritas = (jugador, seleccion, numero, tipo, page, ordenar) => {
  const [figuritas, setFiguritas] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(false)

  useEffect(() => {
    setLoading(true)
    const cargar = async () => {
      try {
        setError(false)
        const data = await explorarFiguritas({ jugador, seleccion, numero, tipo, page, ordenar })
        setFiguritas(data.content)
        setTotalPages(data.totalPages)
        setTotalElements(data.totalElements)
      } catch {
        setError(true)
      } finally {
        setLoading(false)
      }
    }

    const debounce = setTimeout(cargar, 300)
    return () => clearTimeout(debounce)
  }, [jugador, seleccion, numero, tipo, page, ordenar])

  return { figuritas, totalPages, totalElements, loading, error }
}

export default useFiguritas
