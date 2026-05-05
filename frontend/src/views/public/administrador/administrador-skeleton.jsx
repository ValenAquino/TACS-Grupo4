import StatCardSkeleton from './stat-card-skeleton.jsx'
import SeccionSkeleton from './seccion-skeleton.jsx'

const AdministradorSkeleton = () => (
  <>
    <div className="row g-3 mb-3">
      {Array.from({ length: 4 }).map((_, i) => (
        <div className="col-6 col-md-3" key={i}>
          <StatCardSkeleton />
        </div>
      ))}
    </div>

    <div className="row g-3 mb-3">
      <div className="col-12 col-md-6"><SeccionSkeleton filas={3} /></div>
      <div className="col-12 col-md-6"><SeccionSkeleton filas={3} /></div>
    </div>

    <div className="row g-3">
      <div className="col-12 col-md-6"><SeccionSkeleton filas={3} /></div>
    </div>
  </>
)

export default AdministradorSkeleton
