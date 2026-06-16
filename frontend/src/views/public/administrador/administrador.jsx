import useEstadisticasAdmin from '@/hooks/use-estadisticas-admin.js'
import StatCard from './stat-card.jsx'
import SeccionBarras from './seccion-barras.jsx'
import TopSelecciones from './top-selecciones.jsx'
import AdministradorSkeleton from './administrador-skeleton.jsx'
import styles from './administrador.module.css'

const TARJETAS_GLOBALES = (stats) => [
  { icono: 'bi-people-fill', numero: stats.totalUsuarios, label: 'Usuarios registrados' },
  {
    icono: 'bi-collection-fill',
    numero: stats.totalFiguritasPublicadas,
    label: 'Figuritas publicadas',
  },
  {
    icono: 'bi-stopwatch-fill',
    numero: stats.totalSubastasActivas,
    label: 'Subastas activas',
  },
]

const TARJETAS_PERIODO = (stats) => [
  {
    icono: 'bi-tags-fill',
    numero: stats.totalPropuestas,
    label: 'Propuestas realizadas',
    destacado: true,
  },
]

const PROPUESTAS = [
  { label: 'Pendientes', key: 'pendientes', color: '#d49a2c' },
  { label: 'Aceptadas', key: 'aceptadas', color: '#198754' },
  { label: 'Rechazadas', key: 'rechazadas', color: '#dc3545' },
  { label: 'Canceladas', key: 'canceladas', color: '#6c757d' },
  { label: 'Seleccionadas', key: 'seleccionadas', color: '#0d6efd' },
]

const MODALIDADES = [
  { label: 'Solo intercambio', key: 'soloIntercambio', color: '#175a2d' },
  { label: 'Solo subasta', key: 'soloSubasta', color: '#d49a2c' },
  { label: 'Ambos', key: 'ambos', color: '#7c3aed' },
]

const Administrador = () => {
  const { stats, cargando, error, desde, setDesde, hasta, setHasta } = useEstadisticasAdmin()

  const rangoInvalido = desde && hasta && desde > hasta

  return (
    <div className={styles.page}>
      <div className={styles.hero}>
        <div className="d-flex align-items-center gap-2 mb-1">
          <h1 className={styles.heroTitulo}>Panel de administración</h1>
          <span className={styles.adminBadge}>Admin</span>
        </div>
        <p className={styles.heroSubtitulo}>
          Estadísticas de la plataforma Figus Mundial 2026
        </p>
      </div>

      {/* Selector de rango */}
      <div className={`${styles.seccionCard} mb-3`}>
        <p className={styles.seccionTitulo}>Período</p>
        <div className="d-flex align-items-center gap-3 flex-wrap">
          <div className="d-flex align-items-center gap-2">
            <label htmlFor="desde" className="form-label mb-0 text-nowrap">Desde</label>
            <input
              id="desde"
              type="date"
              className="form-control form-control-sm"
              value={desde}
              max={hasta}
              onChange={(e) => setDesde(e.target.value)}
            />
          </div>
          <div className="d-flex align-items-center gap-2">
            <label htmlFor="hasta" className="form-label mb-0 text-nowrap">Hasta</label>
            <input
              id="hasta"
              type="date"
              className="form-control form-control-sm"
              value={hasta}
              min={desde}
              onChange={(e) => setHasta(e.target.value)}
            />
          </div>
        </div>
        {rangoInvalido && (
          <p className="text-danger small mt-2 mb-0">
            &quot;Desde&quot; no puede ser posterior a &quot;Hasta&quot;.
          </p>
        )}
      </div>

      {cargando && <AdministradorSkeleton />}
      {error && <div className="alert alert-danger">{error}</div>}

      {!cargando && !error && stats && (
        <>
          {/* Métricas globales */}
          <p className={`${styles.seccionTitulo} mb-2`}>Globales</p>
          <div className="row g-3 mb-3">
            {TARJETAS_GLOBALES(stats).map((t) => (
              <div className="col-4" key={t.label}>
                <StatCard {...t} />
              </div>
            ))}
          </div>

          {/* Métricas del período */}
          <p className={`${styles.seccionTitulo} mb-2`}>Del período</p>
          <div className="row g-3 mb-3">
            {TARJETAS_PERIODO(stats).map((t) => (
              <div className="col-4" key={t.label}>
                <StatCard {...t} />
              </div>
            ))}
          </div>

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
