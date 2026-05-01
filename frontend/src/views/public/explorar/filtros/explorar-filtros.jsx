import styles from './explorar-filtros.module.css';

const TIPOS = [
  { key: 'todos',       label: 'Todos' },
  { key: 'intercambio', label: 'Intercambio' },
  { key: 'subasta',     label: 'Subasta' },
];

const ExplorarFiltros = ({ tipo, onTipoChange, seleccion, onSeleccionChange, numero, onNumeroChange }) => (
  <div className={styles.filtrosCard}>

    {/* filtros de tipo */}
    <div className={styles.filtrosRow}>
      <div className={styles.filtrosLeft}>
        <span className={styles.filtrosLabel}>Filtros:</span>
        {TIPOS.map(({ key, label }) => (
          <button
            key={key}
            className={`${styles.chip} ${tipo === key ? styles.chipActive : ''}`}
            onClick={() => onTipoChange(key)}
          >
            {label}
          </button>
        ))}
      </div>
    </div>

    {/* Inputs de búsqueda */}
    <div className={styles.filtrosInputRow}>
      <input
        className={styles.filtroInput}
        type="text"
        placeholder="Selección / equipo"
        value={seleccion}
        onChange={e => onSeleccionChange(e.target.value)}
      />
      <input
        className={styles.filtroInput}
        type="text"
        placeholder="Categoría"
      />
      <input
        className={`${styles.filtroInput} ${styles.filtroInputSmall}`}
        type="number"
        placeholder="Nº figurita"
        value={numero}
        onChange={e => onNumeroChange(e.target.value)}
      />
    </div>

  </div>
);

export default ExplorarFiltros;
