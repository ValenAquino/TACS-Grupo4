import { useRef, useState } from 'react'
import styles from './explorar-filtros.module.css'

const TIPOS = [
  { key: 'intercambio', label: 'Intercambio' },
  { key: 'subasta', label: 'Subasta' },
]

const ExplorarFiltros = ({ onAplicar }) => {
  const [tipos, setTipos] = useState([])
  const jugadorRef = useRef(null)
  const seleccionRef = useRef(null)
  const numeroRef = useRef(null)

  const toggleTipo = (key) => {
    setTipos((prev) => (prev.includes(key) ? prev.filter((t) => t !== key) : [...prev, key]))
  }

  const handleAplicar = () => {
    onAplicar({
      tipos,
      jugador: jugadorRef.current.value,
      seleccion: seleccionRef.current.value,
      numero: numeroRef.current.value,
    })
  }

  return (
    <div className={styles.filtrosCard}>
      <div className={styles.filtrosRow}>
        <div className={styles.filtrosLeft}>
          <span className={styles.filtrosLabel}>Filtros:</span>
          {TIPOS.map(({ key, label }) => (
            <button
              key={key}
              className={`${styles.chip} ${tipos.includes(key) ? styles.chipActive : ''}`}
              onClick={() => toggleTipo(key)}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className={styles.filtrosInputRow}>
        <input ref={jugadorRef} className={styles.filtroInput} type="text" placeholder="Jugador" />
        <input
          ref={seleccionRef}
          className={styles.filtroInput}
          type="text"
          placeholder="Selección"
        />
        <input
          ref={numeroRef}
          className={`${styles.filtroInput} ${styles.filtroInputSmall}`}
          type="number"
          placeholder="Nº figurita"
        />
        <button className={`btn ${styles.chipActive}`} onClick={handleAplicar}>
          Aplicar
        </button>
      </div>
    </div>
  )
}

export default ExplorarFiltros
