import styles from './explorar-filtros.module.css'
import useExplorarFiltros, { TIPOS } from './useExplorarFiltros'

const ExplorarFiltros = ({ onAplicar }) => {
  const {
    tipos,
    jugador,
    seleccion,
    numero,
    chipsActivos,
    toggleTipo,
    setJugador,
    setSeleccion,
    setNumero,
    aplicar,
    quitarFiltro,
  } = useExplorarFiltros(onAplicar)

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
        <input
          className={styles.filtroInput}
          type="text"
          placeholder="Jugador"
          value={jugador}
          onChange={(e) => setJugador(e.target.value)}
        />
        <input
          className={styles.filtroInput}
          type="text"
          placeholder="Selección"
          value={seleccion}
          onChange={(e) => setSeleccion(e.target.value)}
        />
        <input
          className={`${styles.filtroInput} ${styles.filtroInputSmall}`}
          type="number"
          placeholder="Nº figurita"
          value={numero}
          onChange={(e) => setNumero(e.target.value)}
        />
        <button className={`btn ${styles.chipActive}`} onClick={aplicar}>
          Aplicar
        </button>
      </div>

      {chipsActivos.length > 0 && (
        <div className={styles.chipsActivos}>
          {chipsActivos.map(({ campo, valor, label }) => (
            <button
              key={`${campo}-${valor}`}
              className={styles.chipActivo}
              onClick={() => quitarFiltro(campo, valor)}
            >
              {label} <span className={styles.chipX}>×</span>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}

export default ExplorarFiltros
