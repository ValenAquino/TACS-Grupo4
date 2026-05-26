import { forwardRef } from 'react'
import styles from './explorar-search.module.css'

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
          onKeyDown={(e) => e.key === 'Enter' && onQueryChange(ref.current.value)}
        />
        <button className={styles.searchBtn} onClick={() => onQueryChange(ref.current.value)}>
          Buscar
        </button>
      </div>
    </div>
  </section>
))

ExplorarSearch.displayName = 'ExplorarSearch'

export default ExplorarSearch
