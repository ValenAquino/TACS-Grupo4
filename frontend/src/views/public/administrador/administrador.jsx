import { useRef } from 'react'
import useEstadisticasAdmin from '@/hooks/use-estadisticas-admin.js'
import StatCard from './stat-card.jsx'
import SeccionBarras from './seccion-barras.jsx'
import TopSelecciones from './top-selecciones.jsx'
import RankingUsuarios from './ranking-usuarios.jsx'
import AdministradorSkeleton from './administrador-skeleton.jsx'
import styles from './administrador.module.css'

const TARJETAS_GLOBALES = (stats) => [
  { icono: 'bi-people-fill', numero: stats.totalUsuarios, label: 'Usuarios registrados' },
  { icono: 'bi-collection-fill', numero: stats.totalFiguritasPublicadas, label: 'Figuritas publicadas' },
  { icono: 'bi-stopwatch-fill', numero: stats.totalSubastasActivas, label: 'Subastas activas' },
]

const PROPUESTAS = [
  { label: 'Pendientes', key: 'pendientes', color: '#d49a2c' },
  { label: 'Aceptadas', key: 'aceptadas', color: '#198754' },
  { label: 'Rechazadas', key: 'rechazadas', color: '#dc3545' },
  { label: 'Canceladas', key: 'canceladas', color: '#6c757d' },
]

const MODALIDADES = [
  { label: 'Solo intercambio', key: 'soloIntercambio', color: '#175a2d' },
  { label: 'Solo subasta', key: 'soloSubasta', color: '#d49a2c' },
  { label: 'Ambos', key: 'ambos', color: '#7c3aed' },
]

const Administrador = () => {
  const desdeRef = useRef(null)
  const hastaRef = useRef(null)

  const {
    stats, cargando, recargando, error,
    desde, setDesde, hasta, setHasta,
    rangoInvalido, aplicar,
  } = useEstadisticasAdmin()

  return (
    <div className={styles.page}>
      <div className={styles.hero}>
        <div className={styles.heroInner}>
          <div className="d-flex align-items-center gap-2 mb-1">
            <h1 className={styles.heroTitulo}>Panel de administración</h1>
            <span className={styles.adminBadge}>Admin</span>
          </div>
          <p className={styles.heroSubtitulo}>Estadísticas de la plataforma Figus Mundial 2026</p>

          <div className={styles.heroPeriodoFiltro}>
            <div className={styles.heroPeriodoCampo} onClick={() => desdeRef.current?.showPicker()}>
              <i
                className="bi bi-calendar3"
                style={{ color: '#9ca3af', fontSize: '0.9rem', flexShrink: 0 }}
              />
              <div style={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
                <span className={styles.heroPeriodoCampoLabel}>Desde</span>
                <input
                  ref={desdeRef}
                  id="desde"
                  type="date"
                  className={styles.heroPeriodoInput}
                  value={desde}
                  onChange={(e) => setDesde(e.target.value)}
                />
              </div>
            </div>
            <span style={{ color: '#d1d5db', flexShrink: 0 }}>—</span>
            <div className={styles.heroPeriodoCampo} onClick={() => hastaRef.current?.showPicker()}>
              <i
                className="bi bi-calendar3"
                style={{ color: '#9ca3af', fontSize: '0.9rem', flexShrink: 0 }}
              />
              <div style={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
                <span className={styles.heroPeriodoCampoLabel}>Hasta</span>
                <input
                  ref={hastaRef}
                  id="hasta"
                  type="date"
                  className={styles.heroPeriodoInput}
                  value={hasta}
                  onChange={(e) => setHasta(e.target.value)}
                />
              </div>
            </div>
            <button
              className={styles.aplicarBtn}
              onClick={aplicar}
              disabled={!!rangoInvalido || recargando}
            >
              {recargando ? (
                <span className="spinner-border spinner-border-sm" role="status" />
              ) : (
                'Aplicar'
              )}
            </button>
          </div>
          {rangoInvalido && (
            <span style={{ color: '#fca5a5', fontSize: '0.82rem' }}>
              &quot;Desde&quot; no puede ser posterior a &quot;Hasta&quot;.
            </span>
          )}
        </div>
      </div>

      <div className={styles.contenido}>
        {cargando && <AdministradorSkeleton />}
        {error && <div className="alert alert-danger">{error}</div>}

        {!cargando && !error && stats && (
          <>
            {/* Estadísticas globales */}
            <p className={styles.seccionHeading}>Estadísticas atemporales</p>

            <div className="row g-3 mb-3">
              {TARJETAS_GLOBALES(stats).map((t) => (
                <div className="col-4" key={t.label}>
                  <StatCard {...t} destacado />
                </div>
              ))}
            </div>

            <div className="row g-3 mb-4">
              <div className="col-12 col-md-6">
                <SeccionBarras
                  titulo="Figuritas por modalidad"
                  icono="bi-pie-chart-fill"
                  config={MODALIDADES}
                  data={stats.figuritasPorModalidad ?? {}}
                />
              </div>
              <div className="col-12 col-md-6">
                <div className={styles.colApilada}>
                  <RankingUsuarios
                    titulo="Mejor reputación"
                    icono="bi-star-fill"
                    iconoColor="#d49a2c"
                    items={stats.rankings?.mejorReputacion}
                    sufijo=" ⭐"
                    decimales={1}
                  />
                  <RankingUsuarios
                    titulo="Más figuritas publicadas"
                    icono="bi-trophy-fill"
                    iconoColor="#d49a2c"
                    items={stats.rankings?.topColeccionistas}
                  />
                </div>
              </div>
            </div>

            {/* Estadísticas del período */}
            <div className={styles.delPeriodoRow}>
              <p className={styles.delPeriodoLabel}>Estadisticas del período</p>
            </div>

            <div className="row g-3 mb-3">
              <div className="col-12 col-md-6">
                <div className={styles.propuestasCard}>
                  <div className={styles.propuestasBarras}>
                    <p className={styles.seccionTitulo}>
                      <i
                        className="bi bi-pie-chart-fill"
                        style={{ color: 'var(--color-secondary)' }}
                      />
                      Propuestas por estado
                    </p>
                    <div className="d-flex flex-column gap-3">
                      {PROPUESTAS.map(({ label, key, color }) => {
                        const valor = stats.propuestasPorEstado?.[key] ?? 0
                        const total = stats.totalPropuestas
                        const pct = total > 0 ? Math.min(100, Math.round((valor / total) * 100)) : 0
                        return (
                          <div key={key} className="d-flex flex-column gap-1">
                            <div className="d-flex justify-content-between align-items-center">
                              <div className="d-flex align-items-center gap-2">
                                <span
                                  style={{
                                    width: 9,
                                    height: 9,
                                    borderRadius: '50%',
                                    background: color,
                                    display: 'inline-block',
                                    flexShrink: 0,
                                  }}
                                />
                                <span style={{ fontSize: '0.85rem', color: 'var(--color-text)' }}>
                                  {label}
                                </span>
                              </div>
                              <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>{valor}</span>
                            </div>
                            <div style={{ background: '#e9ecef', borderRadius: 4, height: 7 }}>
                              <div
                                style={{
                                  width: `${pct}%`,
                                  background: color,
                                  borderRadius: 4,
                                  height: '100%',
                                  transition: 'width 0.4s ease',
                                }}
                              />
                            </div>
                          </div>
                        )
                      })}
                    </div>
                  </div>
                  <div className={styles.totalPropuestasCard}>
                    <i
                      className="bi bi-tags-fill"
                      style={{ color: '#d49a2c', fontSize: '1.4rem' }}
                    />
                    <div className={styles.totalPropuestasNumero}>{stats.totalPropuestas}</div>
                    <div className={styles.totalPropuestasLabel}>Propuestas realizadas</div>
                  </div>
                </div>
              </div>

              <div className="col-12 col-md-6">
                <div className={styles.colApilada}>
                  <RankingUsuarios
                    titulo="Top intercambiadores"
                    icono="bi-arrow-left-right"
                    items={stats.rankings?.topIntercambiadores}
                  />
                  <RankingUsuarios
                    titulo="Top creadores de propuestas"
                    icono="bi-person-plus-fill"
                    items={stats.rankings?.topCreadoresDePropuestas}
                  />
                </div>
              </div>
            </div>

            <div className="row g-3 mb-4">
              <div className="col-12 col-md-6">
                <RankingUsuarios
                  titulo="Top subastadores"
                  icono="bi-hammer"
                  items={stats.rankings?.topSubastadores}
                />
              </div>
              <div className="col-12 col-md-6">
                <RankingUsuarios
                  titulo="Mejor tasa de aceptación"
                  icono="bi-check-circle-fill"
                  items={stats.rankings?.mejorTasaAceptacion}
                  sufijo="%"
                  decimales={0}
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
    </div>
  )
}

export default Administrador
