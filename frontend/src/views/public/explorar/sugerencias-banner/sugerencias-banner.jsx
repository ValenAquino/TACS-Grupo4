import styles from './sugerencias-banner.module.css';
import {useNavigate} from "react-router";

const SugerenciasBanner = () => {
  const navigate = useNavigate();

  return (
      <div className={styles.banner}>
        <div className={styles.text}>
          <span className={styles.icon}>↔</span>
          <div>
            <p className={styles.title}>3 sugerencias encontradas</p>
            <p className={styles.subtitle}>Tenés figuritas que otros quieren, y ellos tienen las tuyas</p>
          </div>
        </div>
        <button className={styles.btn} onClick={() => navigate("/sugerencias")}>Ver sugerencias ↗</button>
      </div>
  )
}

export default SugerenciasBanner;
