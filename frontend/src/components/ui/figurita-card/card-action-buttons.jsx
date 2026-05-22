import { Link } from 'react-router-dom'
import styles from './figurita-card.module.css'

const CardActionBtn = ({ to, label, variant = 'intercambio', state }) => (
  <Link
    to={to}
    state={state}
    className={`${styles.actionBtn} ${variant === 'subasta' ? styles.actionBtnSubasta : ''}`}
  >
    {label}
  </Link>
)

export default CardActionBtn
