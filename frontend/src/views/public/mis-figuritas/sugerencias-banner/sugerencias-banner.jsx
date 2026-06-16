import { Link } from 'react-router-dom'
import styles from './sugerencias-banner.module.css'

const SugerenciasBanner = () => (
  <div className={styles.banner}>
    <div className={styles.text}>
      <span className={styles.icon}>↔</span>
      <div>
        <p className={styles.title}>3 sugerencias encontradas</p>
        <p className={styles.subtitle}>
          Tenés figuritas que otros quieren, y ellos tienen las tuyas
        </p>
      </div>
    </div>
    <Link to="/sugerencias" className={styles.btn}>
      Ver sugerencias ↗
    </Link>
  </div>
)

export default SugerenciasBanner
