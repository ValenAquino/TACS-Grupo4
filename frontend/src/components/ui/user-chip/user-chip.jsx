import renderStars from '@/utils/renderStars'
import styles from './user-chip.module.css'

const UserChip = ({ nombre, reputacion = 0 }) => (
  <div className={styles.chip}>
    <div className={styles.left}>
      <span className={styles.avatar}>{nombre.slice(0, 2).toUpperCase()}</span>
      <span className={styles.name}>{nombre}</span>
    </div>
    <span className={styles.stars}>{renderStars(reputacion)}</span>
  </div>
)

export default UserChip
