import Etiqueta from '../../../../../components/ui/etiqueta/etiqueta.jsx'
import Estrellas from '@/components/ui/estrellas/estrellas.jsx'

const VARIANTE_ESTADO = {
  PENDIENTE: 'advertencia',
  ACEPTADO: 'exito',
  RECHAZADO: 'peligro',
  CANCELADO: 'apagado',
}

const LABEL_ESTADO = {
  PENDIENTE: 'Pendiente',
  ACEPTADO: 'Aceptado',
  RECHAZADO: 'Rechazado',
  CANCELADO: 'Cancelado',
}

const HeaderUsuarioEstado = ({ destinatario, estado }) => (
  <div className="d-flex justify-content-between align-items-center">
    <div className="d-flex align-items-center gap-3">
      <div
        className="rounded-circle d-flex align-items-center justify-content-center fw-bold"
        style={{
          width: '42px',
          height: '42px',
          backgroundColor: 'var(--color-secondary-acent)',
          color: 'var(--color-text)',
        }}
      >
        {destinatario.iniciales}
      </div>

      <div className="d-flex flex-column">
        <span className="fw-semibold">{destinatario.nombre}</span>
        <Estrellas calificacion={destinatario.calificacion_media} mostrarNumero={true} />
      </div>
    </div>

    {estado && (
      <Etiqueta
        label={LABEL_ESTADO[estado] ?? estado}
        variante={VARIANTE_ESTADO[estado] ?? 'secundario'}
      />
    )}
  </div>
)

export default HeaderUsuarioEstado
