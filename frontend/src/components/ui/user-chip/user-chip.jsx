import styles from './user-chip.module.css'

const UserChip = ({ nombre, reputacion = 0 }) => (
  <div className={styles.chip}>
    <span className={styles.avatar}>{nombre.slice(0, 2).toUpperCase()}</span>
    <span className={styles.name}>{nombre}</span>
    <span className={styles.stars}>
      {'★'.repeat(reputacion)}
      {'☆'.repeat(5 - reputacion)}
    </span>
  </div>
)

export default UserChip
