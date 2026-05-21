import { derivarTiempo } from '../../../../../../../utils/subastasTiempo.js'
import useUsuarioActual from '../../../../../../../hooks/useUsuarioActual.js'
import { calificarPerfil } from '../../../../../../../services/perfilService.js'
import CalificarModal from '../../../../../../../components/ui/calificar-modal/calificar-modal.jsx'
import CabeceraFigurita from '../../../../../../../components/ui/cabecera-figurita/cabecera-figurita.jsx'
import BarraTiempo from '../../../../../../../components/ui/barra-tiempo/barra-tiempo.jsx'
import EtiquetaPropuesta from '../../../../../../../components/ui/etiqueta-propuesta/etiqueta-propuesta.jsx'
import { useState } from 'react'
import { useNavigate } from 'react-router'

const BADGE_OFERTA = {
  SELECCIONADO: { label: 'Mejor oferta', className: 'text-success bg-success-subtle' },
  RECHAZADO: { label: 'No elegida', className: 'text-danger bg-danger-subtle' },
  PENDIENTE: { label: 'No elegida', className: 'text-danger bg-danger-subtle' },
  ACEPTADO: { label: 'Elegida', className: 'text-success bg-success-subtle' },
}

const SubastaCard = ({ subasta }) => {
  const { userId } = useUsuarioActual()
  const { id, autor, figurita_subastada, fecha_cierre, tu_oferta } = subasta
  const [mostrarCalificar, setMostrarCalificar] = useState(false)
  const navigate = useNavigate()

  const { finalizada, tiempoRestante, finalizadaHace, finalizaPronto } = derivarTiempo({
    fecha_cierre,
  })

  const badgeEstado = finalizada
    ? null
    : finalizaPronto
      ? { label: '⏱ Finaliza pronto', className: 'text-warning bg-warning-subtle' }
      : { label: 'Activa', className: 'text-success bg-success-subtle' }

  const badgeOferta = tu_oferta.estado ? (BADGE_OFERTA[tu_oferta.estado] ?? null) : null

  const handleCalificar = async ({ valor, descripcion }) => {
    await calificarPerfil(userId, autor.id, {
      valor,
      descripcion,
      transactionId: id,
      tipoTransaccion: 'SUBASTA',
    })
    setMostrarCalificar(false)
  }

  const puedoCalificar = finalizada && tu_oferta.estado === 'ACEPTADO' && !subasta.ya_calificado

  return (
    <div className="border rounded-3 overflow-hidden bg-white">
      <CabeceraFigurita figurita={figurita_subastada} badge={badgeEstado} />

      <BarraTiempo
        finalizada={finalizada}
        tiempoRestante={tiempoRestante}
        finalizadaHace={finalizadaHace}
        derecha={
          <>
            Publicada por <strong>{autor.nombre}</strong>
          </>
        }
      />

      <div className="px-3 py-2 d-flex justify-content-between align-items-center border-top">
        <span className="text-muted" style={{ fontSize: '0.82rem' }}>
          Tu oferta
        </span>
        <div className="d-flex align-items-center gap-2">
          <EtiquetaPropuesta propuesta={tu_oferta.propuesta} />
          {badgeOferta && (
            <span
              className={`badge rounded-pill px-2 py-1 ${badgeOferta.className}`}
              style={{ fontSize: '0.7rem', fontWeight: 500 }}
            >
              {badgeOferta.label}
            </span>
          )}
        </div>
      </div>

      <div className="px-3 py-2 d-flex gap-2 border-top">
        <Button
          label={finalizada ? 'Ver resumen' : 'Ver subasta'}
          variante="secundario_borde"
          className="flex-fill"
          onClick={() => navigate(`/subastas/${subasta.id}`)}
        />
        {finalizada ? (
          <>
            {puedoCalificar && (
              <Button
                label="Calificar usuario"
                variante="secundario_borde"
                className="flex-fill"
                onClick={() => setMostrarCalificar(true)}
              />
            )}
          </>
        ) : (
          <>
            {tu_oferta.figuritas_ofrecidas?.length > 0 && (
              <Button
                label="Mejorar oferta"
                variante="secundario_borde"
                className="flex-fill"
                onClick={() => navigate(`/subastas/${subasta.id}/ofertas/${tu_oferta.id}`)}
              />
            )}
          </>
        )}
      </div>

      <CalificarModal
        show={mostrarCalificar}
        usuario={autor.nombre}
        onConfirmar={handleCalificar}
        onCancelar={() => setMostrarCalificar(false)}
      />
    </div>
  )
}

export default SubastaCard
