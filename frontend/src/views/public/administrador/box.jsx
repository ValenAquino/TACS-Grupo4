import styles from './administrador.module.css'

const Box = ({ w = '100%', h = 14, rounded = 6 }) => (
  <div className={styles.skeletonBox} style={{ width: w, height: h, borderRadius: rounded }} />
)

export default Box
