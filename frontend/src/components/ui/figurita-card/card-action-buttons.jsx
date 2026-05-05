import { Link } from 'react-router-dom'
import styles from './figurita-card.module.css'

const CardActionBtn = ({ to, label, variant = 'intercambio' }) => (
  <Link
    to={to}
    className={`${styles.actionBtn} ${variant === 'subasta' ? styles.actionBtnSubasta : ''}`}
  >
    {label}
  </Link>
)

export default CardActionBtn
