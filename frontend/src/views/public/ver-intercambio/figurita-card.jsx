import styles from './ver-intercambio.module.css'

const FiguritaCard = ({ figurita }) => {
  if (!figurita) return null

  return (
    <div
      className="d-flex align-items-center gap-3 rounded-3 p-3"
      style={{ backgroundColor: '#E1F5EE' }}
    >
      <div className={styles.numeroFigurita}>#{figurita.numero}</div>
      <div>
        <p className="mb-0 fw-semibold" style={{ fontSize: '0.9rem', color: '#085041' }}>
          {figurita.jugador}
        </p>
        <p className="mb-0" style={{ fontSize: '0.75rem', color: '#0F6E56' }}>
          {figurita.seleccion}
          {figurita.posicion ? ` · ${figurita.posicion}` : ''}
        </p>
      </div>
    </div>
  )
}

export default FiguritaCard
