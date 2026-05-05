import { useRef, useState } from 'react'
import styles from './explorar-filtros.module.css'

const TIPOS = [
  { key: 'todos', label: 'Todos' },
  { key: 'intercambio', label: 'Intercambio' },
  { key: 'subasta', label: 'Subasta' },
  { key: 'ambos', label: 'Ambos' },
]

const ExplorarFiltros = ({ onAplicar }) => {
  const [tipo, setTipo] = useState('todos')
  const jugadorRef = useRef(null)
  const seleccionRef = useRef(null)
  const numeroRef = useRef(null)

  const handleAplicar = () => {
    onAplicar({
      tipo,
      jugador: jugadorRef.current.value,
      seleccion: seleccionRef.current.value,
      numero: numeroRef.current.value,
    })
  }

  return (
    <div className={styles.filtrosCard}>

      {/* Chips de tipo */}
      <div className={styles.filtrosRow}>
        <div className={styles.filtrosLeft}>
          <span className={styles.filtrosLabel}>Filtros:</span>
          {TIPOS.map(({ key, label }) => (
            <button
              key={key}
              className={`${styles.chip} ${tipo === key ? styles.chipActive : ''}`}
              onClick={() => setTipo(key)}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* Inputs de búsqueda */}
      <div className={styles.filtrosInputRow}>
        <input
          ref={jugadorRef}
          className={styles.filtroInput}
          type="text"
          placeholder="Jugador"
        />
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
