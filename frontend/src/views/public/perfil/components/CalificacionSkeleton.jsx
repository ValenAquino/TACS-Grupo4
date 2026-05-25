import styles from './CalificacionSkeleton.module.css'

const CalificacionSkeleton = () => (
  <div className={styles.card}>
    <div className={`${styles.bone} ${styles.avatar}`} />
    <div className={styles.body}>
      <div className={`${styles.bone} ${styles.nombre}`} />
      <div className={`${styles.bone} ${styles.estrellas}`} />
      <div className={`${styles.bone} ${styles.descripcion}`} />
    </div>
  </div>
)

export default CalificacionSkeleton
