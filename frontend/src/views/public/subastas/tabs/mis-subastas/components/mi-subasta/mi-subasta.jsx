import { useState } from 'react'
import ConfirmModal from '../../../../../../components/ui/confirm-modal/confirm-modal.jsx'
import CalificarModal from '../../../../../../components/ui/calificar-modal/calificar-modal.jsx'
import CabeceraFigurita from '../../../../../../components/ui/cabecera-figurita/cabecera-figurita.jsx'
import BarraTiempo from '../../../../../../components/ui/barra-tiempo/barra-tiempo.jsx'
import Oferta from './oferta/oferta.jsx'
import {
  seleccionarOferta,
  rechazarOferta,
  cancelarSubasta,
  cerrarSubasta,
} from '../../../../../../services/subastasService.js'
import { calificarPerfil } from '../../../../../../services/perfilService.js'
import { derivarTiempo } from '../../../../../../utils/subastasTiempo.js'
import useUsuarioActual from '../../../../../../hooks/useUsuarioActual.js'
import { useNavigate } from 'react-router'
import './mi-subasta.css'

const BADGE_ESTADO = {
  activa: { label: 'Activa', className: 'text-success bg-success-subtle' },
  finaliza_pronto: { label: 'Finaliza pronto', className: 'text-warning bg-warning-subtle' },
  adjudicada: { label: 'Adjudicada', className: 'text-secondary bg-secondary-subtle' },
}

const MODAL_CONFIG = {
  adjudicar: {
    titulo: 'Adjudicar oferta',
    mensaje: '¿Querés seleccionar esta oferta? Podés cambiarla antes de cerrar la subasta.',
    labelConfirmar: 'Adjudicar',
  },
  rechazar: {
    titulo: 'Rechazar oferta',
    mensaje: '¿Querés rechazar esta oferta? Esta acción no se puede deshacer.',
    labelConfirmar: 'Rechazar',
  },
  cancelar: {
    titulo: 'Cancelar subasta',
    mensaje:
      '¿Querés cancelar esta subasta? Todas las ofertas serán rechazadas. Esta acción no se puede deshacer.',
    labelConfirmar: 'Cancelar subasta',
  },
  cerrar: {
    titulo: 'Cerrar subasta',
    mensaje:
      '¿Querés cerrar esta subasta? La oferta seleccionada será aceptada y el resto rechazadas.',
    labelConfirmar: 'Cerrar subasta',
  },
}

const MiSubasta = ({ subasta, onRefresh }) => {
  const {
    id: subastaId,
    figurita_subastada,
    fecha_cierre,
    ofertas,
    cantidad_ofertas,
    ganador,
    ganador_label,
    ya_calificado: yaCalificado,
  } = subasta

  const { finalizada, tiempoRestante, finalizadaHace, finalizaPronto } = derivarTiempo({
    fecha_cierre,
  })
  const { userId } = useUsuarioActual()
  const navigate = useNavigate()
  const [modal, setModal] = useState(null)
  const [loadingModal, setLoadingModal] = useState(false)
  const [mostrarCalificar, setMostrarCalificar] = useState(false)

  const estadoKey = finalizada ? 'adjudicada' : finalizaPronto ? 'finaliza_pronto' : 'activa'
  const badge = BADGE_ESTADO[estadoKey]
  const haySeleccionada = ofertas?.some((o) => o.seleccionada)
  const config = modal ? MODAL_CONFIG[modal.tipo] : null

  const handleConfirmar = async () => {
    try {
      setLoadingModal(true)
      if (modal.tipo === 'adjudicar') await seleccionarOferta(subastaId, modal.ofertaId)
      else if (modal.tipo === 'rechazar') await rechazarOferta(subastaId, modal.ofertaId)
      else if (modal.tipo === 'cancelar') await cancelarSubasta(subastaId)
      else if (modal.tipo === 'cerrar') await cerrarSubasta(subastaId)
      setModal(null)
      onRefresh()
    } catch {
      // manejar error
    } finally {
      setLoadingModal(false)
    }
  }

  const handleCalificar = async ({ valor, descripcion }) => {
    await calificarPerfil(userId, ganador.id, {
      valor,
      descripcion,
      transactionId: subastaId,
      tipoTransaccion: 'SUBASTA',
    })
    setMostrarCalificar(false)
    onRefresh()
  }

  return (
    <>
      <div className="border rounded-3 overflow-hidden bg-white">
        <CabeceraFigurita figurita={figurita_subastada} badge={badge} />

        <BarraTiempo
          finalizada={finalizada}
          tiempoRestante={tiempoRestante}
          finalizadaHace={finalizadaHace}
          derecha={
            <>
              <strong>{cantidad_ofertas}</strong>{' '}
              {cantidad_ofertas === 1 ? 'oferta recibida' : 'ofertas recibidas'}
            </>
          }
        />

        {/* Ofertas activas */}
        {!finalizada && (
          <div className="px-3 py-2 d-flex flex-column gap-2 border-top">
            <p className="texto-chico mb-0 text-muted">Ofertas recibidas</p>
            {ofertas?.length > 0 ? (
              ofertas.map((oferta) => (
                <Oferta
                  key={oferta.id}
                  oferta={oferta}
                  onAdjudicar={() => setModal({ tipo: 'adjudicar', ofertaId: oferta.id })}
                  onRechazar={() => setModal({ tipo: 'rechazar', ofertaId: oferta.id })}
                />
              ))
            ) : (
              <p className="texto-ganador mb-0 text-muted text-center py-2">
                Todavía no recibiste ofertas para esta subasta.
              </p>
            )}
          </div>
        )}

        {/* Ganador */}
        {finalizada && ganador && (
          <div className="texto-ganador px-3 py-2 border-top">
            <span className="text-muted">Ganador: </span>
            <strong>{ganador.nombre}</strong>
            <span className="text-muted"> · {ganador_label}</span>
          </div>
        )}

        {/* Acciones */}
        <div className="px-3 py-2 d-flex gap-2 border-top">
          {finalizada ? (
            <>
              <button
                className="btn-accion btn btn-outline-secondary flex-fill"
                onClick={() => navigate(`/subastas/${subasta.id}`)}
              >
                Ver resumen
              </button>
              {ganador && !yaCalificado && (
                <button
                  className="btn-accion btn btn-outline-secondary flex-fill"
                  onClick={() => setMostrarCalificar(true)}
                >
                  Calificar usuario
                </button>
              )}
            </>
          ) : (
            <>
              <button
                className="btn-accion btn btn-outline-secondary flex-fill"
                onClick={() => navigate(`/subastas/${subasta.id}`)}
              >
                Ver detalle
              </button>
              <button
                className="btn-accion btn btn-outline-danger flex-fill"
                onClick={() => setModal({ tipo: 'cancelar' })}
              >
                Cancelar subasta
              </button>
              {haySeleccionada && (
                <button
                  className="btn-accion btn btn-success flex-fill"
                  onClick={() => setModal({ tipo: 'cerrar' })}
                >
                  Cerrar subasta
                </button>
              )}
            </>
          )}
        </div>
      </div>

      <ConfirmModal
        show={modal !== null}
        titulo={config?.titulo}
        mensaje={config?.mensaje}
        labelConfirmar={loadingModal ? 'Cargando...' : config?.labelConfirmar}
        onConfirmar={handleConfirmar}
        onCancelar={() => setModal(null)}
      />

      <CalificarModal
        show={mostrarCalificar}
        usuario={ganador?.usuario}
        onConfirmar={handleCalificar}
        onCancelar={() => setMostrarCalificar(false)}
      />
    </>
  )
}

export default MiSubasta
