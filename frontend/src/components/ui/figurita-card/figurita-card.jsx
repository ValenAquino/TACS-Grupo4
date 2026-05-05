import styles from './figurita-card.module.css'
import CardActionBtn from './card-action-buttons'
import UserChip from '@/components/ui/user-chip/user-chip'

const TYPE_LABELS = {
  intercambio: { label: 'intercambio', className: styles.badgeIntercambio },
  subasta:     { label: 'subasta',     className: styles.badgeSubasta },
  ambos:       { label: 'ambos',       className: styles.badgeAmbos },
}

const FiguritaCard = ({
  id,
  number,
  type = 'intercambio',
  emoji,
  emojiBg,
  imageUrl,
  name,
  subtitle,
  available,
  extra,
  user,
}) => {
  const badge = TYPE_LABELS[type] ?? TYPE_LABELS.intercambio

  return (
    <div className={`${styles.card} ${type === 'subasta' ? styles.cardSubasta : ''}`}>

      {/* Número y tipo */}
      <div className={styles.cardHeader}>
        <span className={styles.cardNumber}>#{number}</span>
        <span className={`${styles.badge} ${badge.className}`}>{badge.label}</span>
      </div>

      {/* Imagen / emoji */}
      <div className={styles.cardEmoji} style={emojiBg ? { background: emojiBg } : {}}>
        {imageUrl ? <img src={imageUrl} alt={name} className={styles.cardImage} /> : emoji}
      </div>

      {/* Nombre y subtítulo */}
      <div className={styles.cardInfo}>
        <p className={styles.cardName}>{name}</p>
        <p className={styles.cardSubtitle}>{subtitle}</p>
      </div>

      {/* Disponibilidad, usuario y acción */}
      <div className={styles.cardFooter}>
        {extra ? (
          <span className={styles.cardExtra}>{extra}</span>
        ) : (
          available !== undefined && (
            <span className={styles.cardAvailable}>Disponibles: {available}</span>
          )
        )}

        {user && <UserChip user={user} />}

        {(type === 'intercambio' || type === 'ambos') && (
          <CardActionBtn to={`/intercambios/nuevo?figurita=${id}`} label="Proponer intercambio ↗" />
        )}
        {(type === 'subasta' || type === 'ambos') && (
          <CardActionBtn to={`/subastas?figurita=${id}`} label="Ver subasta ↗" variant="subasta" />
        )}
      </div>

    </div>
  )
}

export default FiguritaCard
