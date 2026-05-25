import styles from './figurita-card-skeleton.module.css'

const FiguritaCardSkeleton = () => (
  <div className={styles.card}>
    <div className={styles.hero} />
    <div className={styles.body}>
      <div className={`${styles.bone} ${styles.boneName}`} />
      <div className={`${styles.bone} ${styles.boneSub}`} />
      <div className={`${styles.bone} ${styles.boneUser}`} />
      <div className={`${styles.bone} ${styles.boneBtn}`} />
    </div>
  </div>
)

export default FiguritaCardSkeleton
