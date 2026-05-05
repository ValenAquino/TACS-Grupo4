import Box from './box.jsx'
import styles from './administrador.module.css'

const SeccionSkeleton = ({ filas = 3 }) => (
  <div className={styles.skeletonCard}>
    <Box w="50%" h={16} />
    <div className="d-flex flex-column gap-3 mt-2">
      {Array.from({ length: filas }).map((_, i) => (
        <div key={i} className="d-flex flex-column gap-1">
          <div className="d-flex justify-content-between">
            <Box w="40%" h={13} />
            <Box w={24} h={13} />
          </div>
          <Box w="100%" h={8} rounded={4} />
        </div>
      ))}
    </div>
  </div>
)

export default SeccionSkeleton
