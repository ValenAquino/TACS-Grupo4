import Estrellas from '../estrellas/estrellas.jsx'
import styles from './user-chip.module.css'

const UserChip = ({ nombre, reputacion = 0 }) => (
  <div className={styles.chip}>
    <div className={styles.left}>
      <span className={styles.avatar}>{nombre.slice(0, 2).toUpperCase()}</span>
      <span className={styles.name}>{nombre}</span>
    </div>
    <Estrellas calificacion={reputacion} variante="dorado" />
  </div>
)

export default UserChip
