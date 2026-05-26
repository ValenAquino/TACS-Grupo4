import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Breadcrumb from '../../../components/ui/breadcrumb/breadcrumb.jsx'
import SectionCard from '../../../components/ui/section-card/section-card.jsx'
import SectionTitle from '../../../components/ui/section-title/section-title.jsx'
import PerfilSimple from '../../../components/ui/perfil-simple/perfil-simple.jsx'
import Button from '../../../components/ui/button/button.jsx'

import {
  obtenerPropuesta,
  aceptarPropuesta,
  rechazarPropuesta,
  cancelarPropuesta,
} from '../../../services/propuestasService.js'

import FiguritaCard from './figurita-card.jsx'
import OfertaCard from './oferta-card.jsx'

import { useError } from '@/contexts/errorContext.jsx'
import { useToast } from '@/contexts/toastContext.jsx'

const VerIntercambio = () => {
  const { intercambioId } = useParams()
  const navigate = useNavigate()
  const [cargando, setCargando] = useState(true)
  const [propuesta, setPropuesta] = useState(null)

  const { handleError } = useError()
  const { showToast } = useToast()

  const cargarIntercambio = async () => {
    try {
      setCargando(true)
      const data = await obtenerPropuesta(intercambioId)
      setPropuesta(data)
    } catch (error) {
      handleError(error, () => {})
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    cargarIntercambio()
  }, [intercambioId])

  const ejecutarAceptar = async () => {
    try {
      await aceptarPropuesta(propuesta.id)

      setPropuesta((prev) => ({
        ...prev,
        estado: 'ACEPTADO',
      }))
      showToast('Propuesta aceptada correctamente.')
    } catch (error) {
      handleError(error, (err) => showToast(err.mensaje, 'error'))
    }
  }

  const ejecutarRechazar = async () => {
    try {
      await rechazarPropuesta(propuesta.id)

      setPropuesta((prev) => ({
        ...prev,
        estado: 'RECHAZADO',
      }))
      showToast('Propuesta rechazada correctamente.')
    } catch (error) {
      handleError(error, (err) => showToast(err.mensaje, 'error'))
    }
  }
  const ejecutarCancelar = async () => {
    try {
      await cancelarPropuesta(propuesta.id)
      setPropuesta((prev) => ({ ...prev, estado: 'CANCELADO' }))
      showToast('Propuesta cancelada correctamente.')
    } catch (error) {
      handleError(error, (err) => showToast(err.mensaje, 'error'))
    }
  }

  if (cargando) {
    return (
      <div className="container py-4">
        <h3>Cargando intercambio...</h3>
      </div>
    )
  }

  if (!propuesta) {
    return (
      <div className="container py-4">
        <h3>No se encontró el intercambio</h3>
      </div>
    )
  }
  const esRecibida = propuesta?.tipo === 'RECIBIDA'
  const esEnviada = propuesta?.tipo === 'ENVIADA'

  const estaPendiente = propuesta.estado === 'PENDIENTE' && esRecibida
  const puedeCancelar = propuesta.estado === 'PENDIENTE' && esEnviada

  return (
    <div className="container py-4 px-3 px-md-4">
      <Breadcrumb
        crumbs={[
          { name: 'Intercambios', to: '/intercambios' },
          { name: `#${propuesta.id}`, to: `/intercambios/${propuesta.id}` },
        ]}
      />

      <SectionCard>
        <SectionTitle>ESTADO DEL INTERCAMBIO</SectionTitle>

        <SectionCard.Section>
          <div className="d-flex justify-content-between align-items-center">
            <span
              className="badge fs-6 fw-semibold px-3 py-2 rounded-3"
              style={
                {
                  ACEPTADO: { backgroundColor: '#E1F5EE', color: '#085041' },
                  RECHAZADO: { backgroundColor: '#FAECE7', color: '#712B13' },
                  CANCELADO: { backgroundColor: '#F1EFE8', color: '#444441' },
                  PENDIENTE: { backgroundColor: '#FAEEDA', color: '#633806' },
                }[propuesta.estado] ?? { backgroundColor: '#F1EFE8', color: '#444441' }
              }
            >
              {propuesta.estado ?? 'PENDIENTE'}
            </span>

            {estaPendiente && (
              <div className="d-flex gap-2">
                <Button label="Rechazar" onClick={ejecutarRechazar} variante="peligroBorde" />
                <Button label="Aceptar" onClick={ejecutarAceptar} variante="exitoBorde" />
              </div>
            )}
            {puedeCancelar && <Button label="Cancelar" onClick={ejecutarCancelar} variante="peligroBorde" />}
          </div>
        </SectionCard.Section>
      </SectionCard>

      <SectionCard>
        <SectionTitle>USUARIO</SectionTitle>

        <SectionCard.Section>
          <PerfilSimple perfil={esRecibida ? propuesta.autor : propuesta.destinatario} />
        </SectionCard.Section>
      </SectionCard>

      <SectionCard>
        <SectionTitle>
          {esRecibida ? 'FIGURITA QUE VOS ENTREGÁS' : 'FIGURITA QUE SOLICITÁS'}
        </SectionTitle>
        <SectionCard.Section>
          <FiguritaCard figurita={propuesta.figurita_buscada} />
        </SectionCard.Section>
      </SectionCard>

      <SectionCard>
        <SectionTitle>
          {`${esRecibida ? 'FIGURITAS QUE VOS RECIBÍS' : 'FIGURITAS QUE OFRECÉS'} (${propuesta.figuritas_ofrecidas.length})`}
        </SectionTitle>
        <SectionCard.Section>
          <div className="d-flex flex-column gap-3">
            {propuesta.figuritas_ofrecidas.map((fig) => (
              <FiguritaCard key={fig.id} figurita={fig} />
            ))}
          </div>
        </SectionCard.Section>
      </SectionCard>

      <SectionCard>
        <SectionTitle>RESUMEN DE LA PROPUESTA</SectionTitle>

        <SectionCard.Section>
          <OfertaCard propuesta={propuesta} tipo={propuesta.tipo} />
        </SectionCard.Section>
      </SectionCard>
    </div>
  )
}

export default VerIntercambio
