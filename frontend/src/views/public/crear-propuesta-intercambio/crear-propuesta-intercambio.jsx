import { useLocation } from 'react-router-dom'
import SectionCard from '@/components/ui/section-card/section-card.jsx'
import SectionTitle from '@/components/ui/section-title/section-title.jsx'
import SelectorRepetidas from '@/components/ui/selector-repetidas/selector-repetidas.jsx'
import Button from '@/components/ui/button/button.jsx'
import useCrearPropuesta from './useCrearPropuesta'
import styles from './crear-propuesta-intercambio.module.css'

const CrearPropuestaIntercambio = () => {
  const { state } = useLocation()
  const figurita = state?.figurita

  if (!figurita) return <h2>No se pudo cargar la figurita.</h2>

  const { seleccionadas, setSeleccionadas, enviar } = useCrearPropuesta(figurita)

  return (
    <div className="d-flex flex-column gap-3">
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
            <p className={styles.crearPropuestaDuenio}>{figurita.nombre_usuario}</p>
          </div>
        </SectionCard.Section>
      </SectionCard>

      <div className="d-flex flex-column gap-2">
        <p className={styles.crearPropuestaSeleccionTitulo}>
          Seleccioná las figurita que querés ofrecer
        </p>
        <SelectorRepetidas modo="multiple" bloqueadas={[]} onChange={setSeleccionadas} metodoIntercambio = "INTERCAMBIO"/>
      </div>

      <Button label="Enviar propuesta ↗" disabled={seleccionadas.length === 0} onClick={enviar} />
    </div>
  )
}

export default CrearPropuestaIntercambio
