import Box from './box.jsx'
import styles from './administrador.module.css'

const StatCardSkeleton = () => (
  <div className={styles.skeletonCard}>
    <Box w={32} h={32} rounded={8} />
    <Box w="55%" h={36} />
    <Box w="80%" h={14} />
  </div>
)

export default StatCardSkeleton
