import styles from '../administrador.module.css'

const StatCard = ({ icono, numero, label, destacado }) => (
  <div className={styles.statCard}>
    <i className={`bi ${icono} ${styles.statIcon} ${destacado ? styles.statIconDestacado : ''}`} />
    <div className={`${styles.statNumero} ${destacado ? styles.statNumeroDestacado : ''}`}>
      {numero}
    </div>
    <div className={styles.statLabel}>{label}</div>
  </div>
)

export default StatCard
