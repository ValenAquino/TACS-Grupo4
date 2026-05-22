import { useLocation, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { crearPropuesta } from '@/services/propuestasService.js'
import SectionCard from '@/components/ui/section-card/section-card.jsx'
import SectionTitle from '@/components/ui/section-title/section-title.jsx'
import SelectorRepetidas from '@/components/ui/selector-repetidas/selector-repetidas.jsx'
import Button from '@/components/ui/button/button.jsx'
import styles from './crear-propuesta-intercambio.module.css'

const CrearPropuestaIntercambio = () => {
  const { state } = useLocation()
  const navigate = useNavigate()
  const figurita = state?.figurita
  console.log(figurita)

  const [seleccionadas, setSeleccionadas] = useState([])

  if (!figurita) return <h2>No se pudo cargar la figurita.</h2>

  const onEnviar = async () => {
    await crearPropuesta(
      figurita.usuarioId,
      figurita.figuritaId,
      seleccionadas.map((f) => f.figuritaId),
    )
    navigate('/intercambios')
  }

  return (
    <div className="d-flex flex-column gap-3">
      {/* Preview figurita objetivo */}
      <div
        className={
          styles.figuritaAIntercambiar +
          ' p-2 d-flex flex-column justify-content-center align-items-center gap-2 w-100 rounded-2 mb-3'
        }
      >
        <div className={styles.figuritaImagen + ' bg-white rounded-3 '}></div>

        <h4 className={'text-white'}>{figurita.jugador}</h4>
        <h6 className={'text-white'}>{figurita.seleccion}</h6>
      </div>

      <SectionCard>
        <SectionTitle>FIGURITA QUE QUERÉS OBTENER</SectionTitle>
        <SectionCard.Section>
          <div className="d-flex flex-column gap-1">
            <p className={styles.crearPropuestaLabel}>Publicada por</p>
            <p className={styles.crearPropuestaDuenio}>{figurita.nombreUsuario}</p>
          </div>
        </SectionCard.Section>
      </SectionCard>

      <div className="d-flex flex-column gap-2">
        <p className={styles.crearPropuestaSeleccionTitulo}>
          Seleccioná la figurita que querés ofrecer
        </p>
        <SelectorRepetidas modo="unica" bloqueadas={[]} onChange={setSeleccionadas} />
      </div>

      <Button label="Enviar propuesta ↗" disabled={seleccionadas.length === 0} onClick={onEnviar} />
    </div>
  )
}

export default CrearPropuestaIntercambio
