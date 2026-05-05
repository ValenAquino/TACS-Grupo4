import styles from './figurita-card.module.css';

const TYPE_LABELS = {
  intercambio: { label: 'intercambio', className: styles.badgeIntercambio },
  subasta:     { label: 'subasta',     className: styles.badgeSubasta },
  ambos:       { label: 'ambos',       className: styles.badgeAmbos },
};

const FiguritaCard = ({
  number,
  type = 'intercambio',
  emoji,
  emojiBg,
  imageUrl,
  name,
  subtitle,
  available,
  actionLabel,
  onAction,
  extra,
  user,
}) => {
  const badge = TYPE_LABELS[type] ?? TYPE_LABELS.intercambio;
  const resolvedLabel = actionLabel ?? (type === 'subasta' ? 'Ver subasta ↗' : 'Proponer intercambio ↗');

  return (
    <div className={`${styles.card} ${type === 'subasta' ? styles.cardSubasta : ''}`}>

      {/* Número y tipo */}
      <div className={styles.cardHeader}>
        <span className={styles.cardNumber}>#{number}</span>
        <span className={`${styles.badge} ${badge.className}`}>{badge.label}</span>
      </div>

      {/* Imagen / emoji */}
      <div className={styles.cardEmoji} style={emojiBg ? { background: emojiBg } : {}}>
        {imageUrl
          ? <img src={imageUrl} alt={name} className={styles.cardImage} />
          : emoji
        }
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

        {user && (
          <div className={styles.userChip}>
            <span className={styles.userAvatar} style={{ background: user.color }}>
              {user.initials}
            </span>
            <span className={styles.userName}>{user.name}</span>
            <span className={styles.userStars}>
              {'★'.repeat(user.stars)}{'☆'.repeat(5 - user.stars)}
            </span>
          </div>
        )}

        {type === 'ambos' ? (
          <>
            <button className={styles.actionBtn} onClick={() => onAction('intercambio')}>
              Proponer intercambio ↗
            </button>
            <button className={`${styles.actionBtn} ${styles.actionBtnSubasta}`} onClick={() => onAction('subasta')}>
              Ver subasta ↗
            </button>
          </>
        ) : (
          <button
            className={`${styles.actionBtn} ${type === 'subasta' ? styles.actionBtnSubasta : ''}`}
            onClick={() => onAction(type)}
          >
            {resolvedLabel}
          </button>
        )}

      </div>
    </div>
  );
};

export default FiguritaCard;
