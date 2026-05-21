import { useEffect, useState } from 'react'
import { explorarFiguritas } from '@/services/explorarService'
import {useError} from "@/contexts/errorContext.jsx";

const useFiguritas = (q, jugador, seleccion, numero, tipo, page) => {
  const [figuritas, setFiguritas] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(false)
  const {handleError} = useError()

  useEffect(() => {
    setLoading(true)
    const cargar = async () => {
      try {
        setError(false)
        const data = await explorarFiguritas({ q, jugador, seleccion, numero, tipo, page })
        setFiguritas(data.content)
        setTotalPages(data.totalPages)
        setTotalElements(data.totalElements)
      } catch (error) {
        handleError(error, () => {})
      } finally {
        setLoading(false)
      }
    }

    cargar()
  }, [q, jugador, seleccion, numero, tipo, page])

  return { figuritas, totalPages, totalElements, loading, error }
}

export default useFiguritas
