import { resolverTipo } from '@/utils/figuritas'
import styles from './figurita-card.module.css'
import CardActionBtn from './card-action-buttons'
import UserChip from '@/components/ui/user-chip/user-chip'

const TYPE_LABELS = {
  intercambio: { label: 'intercambio', className: styles.badgeIntercambio },
  subasta: { label: 'subasta', className: styles.badgeSubasta },
  ambos: { label: 'ambos', className: styles.badgeAmbos },
}

const FiguritaCard = ({
  figuritaId,
  numero,
  metodos = [],
  strCutout,
  jugador,
  seleccion,
  cantidadExistente,
  nombreUsuario,
  reputacion,
  extra,
  figurita,
}) => {
  const tipo = resolverTipo(metodos)
  const badge = TYPE_LABELS[tipo] ?? TYPE_LABELS.intercambio

  return (
    <div className={`${styles.card} ${tipo === 'subasta' ? styles.cardSubasta : ''}`}>
      <div className={styles.cardHeader}>
        <span className={styles.cardNumber}>#{numero}</span>
        <span className={`${styles.badge} ${badge.className}`}>{badge.label}</span>
      </div>

      <div className={styles.cardEmoji}>
        {strCutout ? (
          <img src={strCutout} alt={jugador} className={styles.cardImage} />
        ) : (
          <span className={styles.cardImagePlaceholder} />
        )}
      </div>

      <div className={styles.cardInfo}>
        <p className={styles.cardName}>{jugador}</p>
        <p className={styles.cardSubtitle}>{seleccion}</p>
      </div>

      <div className={styles.cardFooter}>
        {extra ? (
          <span className={styles.cardExtra}>{extra}</span>
        ) : (
          cantidadExistente !== undefined && (
            <span className={styles.cardAvailable}>Disponibles: {cantidadExistente}</span>
          )
        )}

        {nombreUsuario && <UserChip nombre={nombreUsuario} reputacion={reputacion} />}

        {(tipo === 'intercambio' || tipo === 'ambos') && (
          <CardActionBtn
            to="/intercambios/crear"
            label="Proponer intercambio ↗"
            state={{ figurita }}
          />
        )}
        {(tipo === 'subasta' || tipo === 'ambos') && (
          <CardActionBtn
            to={`/subastas?figurita=${figuritaId}`}
            label="Ver subasta ↗"
            variant="subasta"
          />
        )}
      </div>
    </div>
  )
}

export default FiguritaCard
