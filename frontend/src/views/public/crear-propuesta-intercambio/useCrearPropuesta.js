import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { crearPropuesta } from '@/services/propuestasService.js'
import { useError } from '@/contexts/errorContext.jsx'
import { useToast } from '@/contexts/toastContext.jsx'

const useCrearPropuesta = (figurita) => {
  const [seleccionadas, setSeleccionadas] = useState([])
  const navigate = useNavigate()
  const { handleError } = useError()
  const { showToast } = useToast()

  const enviar = async () => {
    try {
      await crearPropuesta(
        figurita.perfil_id,
        figurita.figurita_id,
        seleccionadas.map((f) => f.figurita_id),
      )
      navigate('/intercambios')
    } catch (e) {
      handleError(e, (err) => showToast(err.mensaje, 'error'))
    }
  }

  return { seleccionadas, setSeleccionadas, enviar }
}

export default useCrearPropuesta
