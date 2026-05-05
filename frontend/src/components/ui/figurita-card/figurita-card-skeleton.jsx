import styles from './figurita-card-skeleton.module.css'

const FiguritaCardSkeleton = () => (
  <div className={styles.card}>
    {/* Número y tipo */}
    <div className="d-flex justify-content-between align-items-center px-3 pt-3">
      <div className={`${styles.bone} ${styles.boneXs}`} />
      <div className={`${styles.bone} ${styles.boneSm}`} />
    </div>

    {/* Imagen / emoji */}
    <div className={`${styles.bone} ${styles.boneEmoji} mx-3 my-2 rounded-2`} />

    {/* Nombre y subtítulo */}
    <div className="d-flex flex-column align-items-center gap-2 px-3">
      <div className={`${styles.bone} ${styles.boneMd}`} />
      <div className={`${styles.bone} ${styles.boneXs}`} />
    </div>

    {/* Disponibilidad, usuario y acción */}
    <div className="px-3 pb-3 pt-2 d-flex flex-column gap-2">
      <div className={`${styles.bone} ${styles.boneXs}`} />
      <div className={`${styles.bone} ${styles.boneUser}`} />
      <div className={`${styles.bone} ${styles.boneBtn} rounded-2`} />
    </div>
  </div>
)

export default FiguritaCardSkeleton
