import { forwardRef } from 'react'
import styles from './explorar-search.module.css'

const STATS = [
  { value: '12.847', label: 'FIGURITAS DISPONIBLES' },
  { value: '3.291', label: 'USUARIOS ACTIVOS' },
  { value: '842', label: 'INTERCAMBIOS HOY' },
]

const ExplorarSearch = forwardRef(({ onQueryChange }, ref) => (
  <section className={styles.hero}>
    <div className={styles.heroContent}>
      <h1 className={styles.heroTitle}>Encontrá la figurita que te falta</h1>
      <p className={styles.heroSubtitle}>
        Buscá entre miles de repetidas disponibles para intercambio
      </p>

      <div className={styles.searchBar}>
        <span className={styles.searchIcon}>🔍</span>
        <input
          ref={ref}
          className={styles.searchInput}
          type="text"
          placeholder="Buscar por número, jugador o selección..."
        />
        <button className={styles.searchBtn} onClick={() => onQueryChange(ref.current.value)}>
          Buscar
        </button>
      </div>

      <div className={styles.stats}>
        {STATS.map((stat) => (
          <div key={stat.label} className={styles.stat}>
            <span className={styles.statValue}>{stat.value}</span>
            <span className={styles.statLabel}>{stat.label}</span>
          </div>
        ))}
      </div>
    </div>
  </section>
))

ExplorarSearch.displayName = 'ExplorarSearch'

export default ExplorarSearch
