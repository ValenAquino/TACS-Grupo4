import useEstadisticasAdmin from '../../../hooks/use-estadisticas-admin.js'
import StatCard from './stat-card.jsx'
import SeccionBarras from './seccion-barras.jsx'
import TopSelecciones from './top-selecciones.jsx'
import AdministradorSkeleton from './administrador-skeleton.jsx'
import styles from './administrador.module.css'

const TARJETAS = (stats) => [
  { icono: 'bi-people-fill', numero: stats.totalUsuarios, label: 'Usuarios registrados' },
  {
    icono: 'bi-collection-fill',
    numero: stats.totalFiguritasPublicadas,
    label: 'Figuritas publicadas',
  },
  {
    icono: 'bi-tags-fill',
    numero: stats.totalPropuestas,
    label: 'Propuestas realizadas',
    destacado: true,
  },
  {
    icono: 'bi-stopwatch-fill',
    numero: stats.totalSubastasActivas,
    label: 'Subastas activas',
    destacado: true,
  },
]

const PROPUESTAS = [
  { label: 'Pendientes', key: 'pendientes', color: '#d49a2c' },
  { label: 'Aceptadas', key: 'aceptadas', color: '#198754' },
  { label: 'Rechazadas', key: 'rechazadas', color: '#dc3545' },
]

const MODALIDADES = [
  { label: 'Solo intercambio', key: 'soloIntercambio', color: '#175a2d' },
  { label: 'Solo subasta', key: 'soloSubasta', color: '#d49a2c' },
  { label: 'Ambos', key: 'ambos', color: '#7c3aed' },
]

const Administrador = () => {
  const { stats, cargando, error } = useEstadisticasAdmin()

  return (
    <div className={styles.page}>
      {/* Encabezado */}
      <div className={styles.hero}>
        <div className="d-flex align-items-center gap-2 mb-1">
          <h1 className={styles.heroTitulo}>Panel de administración</h1>
          <span className={styles.adminBadge}>Admin</span>
        </div>
        <p className={styles.heroSubtitulo}>
          Estadísticas globales de la plataforma Figus Mundial 2026
        </p>
      </div>

      {/* Skeleton de carga */}
      {cargando && <AdministradorSkeleton />}

      {/* Error */}
      {error && <div className="alert alert-danger">{error}</div>}

      {!cargando && !error && stats && (
        <>
          {/* Tarjetas de resumen */}
          <div className="row g-3 mb-3">
            {TARJETAS(stats).map((t) => (
              <div className="col-6 col-md-3" key={t.label}>
                <StatCard {...t} />
              </div>
            ))}
          </div>

          {/* Gráficos de barras */}
          <div className="row g-3 mb-3">
            <div className="col-12 col-md-6">
              <SeccionBarras
                titulo="Propuestas por estado"
                config={PROPUESTAS}
                data={stats.propuestasPorEstado ?? {}}
              />
            </div>
            <div className="col-12 col-md-6">
              <SeccionBarras
                titulo="Figuritas por modalidad"
                config={MODALIDADES}
                data={stats.figuritasPorModalidad ?? {}}
              />
            </div>
          </div>

          {/* Ranking de selecciones */}
          {stats.topSelecciones?.length > 0 && (
            <div className="row g-3">
              <div className="col-12">
                <TopSelecciones data={stats.topSelecciones} />
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default Administrador
