import BarraItem from './barra-item'
import styles from '../administrador.module.css'

const FLAG_MAP = {
  ARGENTINA: '🇦🇷',
  BRASIL: '🇧🇷',
  ESPAÑA: '🇪🇸',
  FRANCIA: '🇫🇷',
  ALEMANIA: '🇩🇪',
  ITALIA: '🇮🇹',
  PORTUGAL: '🇵🇹',
  URUGUAY: '🇺🇾',
  MEXICO: '🇲🇽',
  COLOMBIA: '🇨🇴',
  CHILE: '🇨🇱',
  JAPON: '🇯🇵',
  INGLATERRA: '🏴󠁧󠁢󠁥󠁮󠁧󠁿',
  HOLANDA: '🇳🇱',
  CROACIA: '🇭🇷',
}

const COLORES = [
  '#2563eb',
  '#16a34a',
  '#ea580c',
  '#7c3aed',
  '#d49a2c',
  '#0891b2',
  '#db2777',
  '#65a30d',
]

const TopSelecciones = ({ data }) => {
  const items = data.map((s, i) => ({
    label: `${FLAG_MAP[s.seleccion] ?? '🌍'} ${s.seleccion.charAt(0) + s.seleccion.slice(1).toLowerCase()}`,
    valor: s.cantidad,
    color: COLORES[i % COLORES.length],
  }))

  const max = Math.max(...items.map((s) => s.valor), 1)

  return (
    <div className={styles.seccionCard}>
      <p className={styles.seccionTitulo}>Top selecciones publicadas</p>
      <div className="d-flex flex-column gap-3">
        {items.map((s) => (
          <BarraItem key={s.label} label={s.label} valor={s.valor} total={max} color={s.color} />
        ))}
      </div>
    </div>
  )
}

export default TopSelecciones
