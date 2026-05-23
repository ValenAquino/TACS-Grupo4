import styles from './figurita-info.module.css'

const FiguritaInfo = ({ fig, opaco }) => (
  <div className={styles['scroll-fila-info'] + (opaco ? ' ' + styles['opaco'] : '')}>
    <p className={styles['scroll-fila-nombre']}>{fig.jugador}</p>
    <p className={styles['scroll-fila-detalle']}>
      {fig.seleccion} · #{fig.numero}
    </p>
  </div>
)

export default FiguritaInfo
