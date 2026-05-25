const VARIANTES = {
  exito: 'text-success bg-success-subtle fw-semibold',
  peligro: 'text-danger bg-danger-subtle fw-semibold',
  advertencia: 'text-warning-emphasis bg-warning-subtle fw-semibold',
  secundario: 'text-secondary bg-secondary-subtle fw-semibold',
  apagado: 'text-muted bg-light fw-semibold',
}

const Etiqueta = ({ label, variante = 'secundario' }) => (
  <span
    className={`badge rounded-pill px-2 py-1 ${VARIANTES[variante]}`}
    style={{ fontSize: '0.7rem', fontWeight: 500 }}
  >
    {label}
  </span>
)

export default Etiqueta