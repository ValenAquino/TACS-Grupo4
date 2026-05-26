import styles from './explorar-filtros.module.css'

const FiltrosActivos = ({ chips, onQuitar }) => {
  if (chips.length === 0) return null

  return (
    <div className={styles.chipsActivos}>
      {chips.map(({ campo, valor, label }) => (
        <button
          key={`${campo}-${valor}`}
          className={styles.chipActivo}
          onClick={() => onQuitar(campo, valor)}
        >
          {label} <span className={styles.chipX}>×</span>
        </button>
      ))}
    </div>
  )
}

export default FiltrosActivos
