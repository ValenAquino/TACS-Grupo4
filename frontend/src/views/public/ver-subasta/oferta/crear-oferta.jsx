import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { buscarSubasta, crearOferta } from '@/services/subastasService.js'
import { buscarPerfil } from '@/services/perfilService.js'
import SectionTitle from '@/components/ui/section-title/section-title.jsx'
import SectionCard from '@/components/ui/section-card/section-card.jsx'
import ScrollRepetidas from '@/components/ui/scroll-figuritas/scroll-repetidas.jsx'
import useUsuarioActual from '@/hooks/useUsuarioActual.js'
import styles from '../ver-subasta.module.css'

// ─── Helpers ──────────────────────────────────────────────────────────────────

// Dado el array de repetidas del usuario y las figuritas solicitadas por la subasta,
// devuelve las repetidas que coinciden con las solicitadas (para marcarlas como obligatorias).
// Si el usuario no tiene alguna solicitada, se filtra afuera — eso se detecta aparte.
const obtenerBloqueadas = (repetidas, figuritasSolicitadas) => {
  if (!figuritasSolicitadas?.length) return []
  const idsRepetidas = new Set(repetidas.map((r) => r.figurita_id))
  return figuritasSolicitadas
    .filter((sol) => idsRepetidas.has(sol.id))
    .map((sol) => repetidas.find((r) => r.figurita_id === sol.id))
}

// Figuritas solicitadas que el usuario directamente no tiene entre sus repetidas.
const obtenerFaltantesRequeridas = (repetidas, figuritasSolicitadas) => {
  if (!figuritasSolicitadas?.length) return []
  const idsRepetidas = new Set(repetidas.map((r) => r.figurita_id))
  return figuritasSolicitadas.filter((sol) => !idsRepetidas.has(sol.id))
}

// ─── CrearOferta ──────────────────────────────────────────────────────────────
const CrearOferta = () => {
  const { subId } = useParams()
  const { userId } = useUsuarioActual()
  const [subasta, setSubasta] = useState(null)
  const [repetidas, setRepetidas] = useState([])
  const [calificacionUsuario, setCalificacionUsuario] = useState(null)
  const [cargando, setCargando] = useState(true)

  // Solo las figuritas opcionales que el usuario eligió de más
  const [figuritasExtra, setFiguritasExtra] = useState([])

  useEffect(() => {
    const cargar = async () => {
      try {
        setCargando(true)
        const [payloadSubasta, payloadRepetidas, payloadPerfil] = await Promise.all([
          buscarSubasta({ subId }),
          //buscarRepetidas(userId),
          buscarPerfil(userId),
        ])
        setSubasta(payloadSubasta)
        setRepetidas(payloadRepetidas ?? [])
        setCalificacionUsuario(payloadPerfil?.calificacion ?? null)
      } catch (error) {
        console.error('Error al cargar datos:', error)
      } finally {
        setCargando(false)
      }
    }
    cargar()
  }, [subId, userId])

  if (cargando) return <h2>Cargando...</h2>
  if (!subasta) return <h2>No se pudo cargar la subasta.</h2>

  // ── Validación de calificación ─────────────────────────────────────────────
  const calMinima = subasta.calificacion_minima_solicitada ?? 0
  const cumpleCalificacion =
    calMinima === 0 || (calificacionUsuario !== null && calificacionUsuario >= calMinima)

  // ── Figuritas bloqueadas (obligatorias) y faltantes ────────────────────────
  const bloqueadas = obtenerBloqueadas(repetidas, subasta.figuritas_solicitadas)
  const faltantesRequeridas = obtenerFaltantesRequeridas(repetidas, subasta.figuritas_solicitadas)

  // El usuario no puede ofertar si le falta alguna requerida
  const tieneTodassLasRequeridas = faltantesRequeridas.length === 0

  // ── Toggle de extras ───────────────────────────────────────────────────────
  // Las bloqueadas no pasan por acá, onToggle solo aplica a las opcionales
  const toggleExtra = (fig) => {
    const esBloqueada = bloqueadas.some((b) => b.figurita_id === fig.figurita_id)
    if (esBloqueada) return // no debería pasar, pero por las dudas
    const existe = figuritasExtra.some((f) => f.figurita_id === fig.figurita_id)
    setFiguritasExtra(
      existe
        ? figuritasExtra.filter((f) => f.figurita_id !== fig.figurita_id)
        : [...figuritasExtra, fig],
    )
  }

  // Las figuritas finales de la oferta = bloqueadas + extras elegidas
  const todasSeleccionadas = [...bloqueadas, ...figuritasExtra]
  const puedeOfertar = cumpleCalificacion && tieneTodassLasRequeridas

  const onEnviar = async () => {
    await crearOferta(
      subId,
      userId,
      todasSeleccionadas.map((f) => f.figurita_id),
    )
    navigate("/subastas");
  }

  return (
    <div>
      <div
        className={
          styles.figuritaSubastada +
          ' p-2 d-flex flex-column justify-content-center align-items-center gap-2 w-100 rounded-2 mb-3'
        }
      >
        <div className={styles.figuritaImagen + ' bg-white rounded-3 '}></div>

        <h4 className={'text-white'}>{subasta.figurita.jugador}</h4>
        <h6 className={'text-white'}>{subasta.figurita.seleccion}</h6>
      </div>

      {/* Condiciones mínimas */}
      <SectionCard>
        <SectionTitle>CONDICIONES PARA OFERTAR</SectionTitle>
        <SectionCard.Section>
          <div className="d-flex flex-column gap-3">
            {/* Figuritas requeridas — siempre visible */}
            <div className="d-flex flex-column gap-2">
              <p
                className="mb-0 text-uppercase fw-semibold text-muted"
                style={{ fontSize: '0.72rem', letterSpacing: '0.06em' }}
              >
                Figuritas requeridas
              </p>
              {subasta.figuritas_solicitadas.length > 0 ? (
                <>
                  <div className="d-flex flex-column gap-2">
                    {subasta.figuritas_solicitadas.map((fig, i) => (
                      <div
                        key={i}
                        className="d-flex align-items-center gap-2 px-3 py-2 rounded-3"
                        style={{ backgroundColor: '#E1F5EE' }}
                      >
                        <div
                          className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                          style={{ width: 32, height: 32, backgroundColor: '#0F6E56' }}
                        >
                          <span style={{ fontSize: '0.7rem', fontWeight: 500, color: '#E1F5EE' }}>
                            {fig.numero}
                          </span>
                        </div>
                        <div>
                          <p
                            className="mb-0 fw-semibold"
                            style={{ fontSize: '0.85rem', color: '#085041' }}
                          >
                            {fig.jugador}
                          </p>
                          <p className="mb-0" style={{ fontSize: '0.72rem', color: '#0F6E56' }}>
                            {fig.seleccion}
                          </p>
                        </div>
                      </div>
                    ))}
                  </div>
                  <p className="mb-0 text-muted" style={{ fontSize: '0.72rem' }}>
                    El ofertante debe incluir al menos una de estas figuritas
                  </p>
                </>
              ) : (
                <p
                  className="mb-0 fst-italic"
                  style={{ fontSize: '0.85rem', color: 'var(--color-text-tertiary)' }}
                >
                  Sin restricción — el ofertante puede ofrecer cualquier figurita
                </p>
              )}
            </div>

            {/* Calificación mínima — siempre visible */}
            <div
              className="d-flex align-items-center justify-content-between pt-2"
              style={{ borderTop: '0.5px solid #9FE1CB' }}
            >
              <div>
                <p
                  className="mb-0 text-uppercase fw-semibold text-muted"
                  style={{ fontSize: '0.72rem', letterSpacing: '0.06em' }}
                >
                  Calificación mínima
                </p>
                <p className="mb-0 text-muted" style={{ fontSize: '0.72rem' }}>
                  {subasta.calificacion_minima <= 1
                    ? 'Cualquier usuario puede ofertar'
                    : 'Solo usuarios con esta calificación pueden ofertar'}
                </p>
              </div>
              {subasta.calificacion_minima <= 1 ? (
                <div
                  className="px-3 py-2 rounded-3 flex-shrink-0"
                  style={{
                    backgroundColor: 'var(--bs-secondary-bg)',
                    border: '0.5px solid var(--bs-border-color)',
                  }}
                >
                  <span style={{ fontSize: '0.82rem', color: 'var(--bs-secondary-color)' }}>
                    Sin mínimo
                  </span>
                </div>
              ) : (
                <div
                  className="d-flex align-items-center gap-1 px-3 py-2 rounded-3 flex-shrink-0"
                  style={{ backgroundColor: '#E1F5EE' }}
                >
                  <span className="fw-semibold" style={{ fontSize: '0.95rem', color: '#085041' }}>
                    {subasta.calificacion_minima_solicitada}
                  </span>
                  <span style={{ fontSize: '0.82rem', color: '#0F6E56' }}>★ o más</span>
                </div>
              )}
            </div>
          </div>
        </SectionCard.Section>
      </SectionCard>

      {/* Bloqueo por calificación */}
      {!cumpleCalificacion && (
        <div className="alert alert-warning mt-3" style={{ fontSize: '0.88rem' }}>
          Tu calificación actual ({calificacionUsuario ?? 'sin datos'}★) es menor a la requerida (
          {calMinima}★). No podés ofertar en esta subasta.
        </div>
      )}

      {/* Bloqueo por figuritas requeridas que no tiene */}
      {cumpleCalificacion && !tieneTodassLasRequeridas && (
        <div className="alert alert-warning mt-3" style={{ fontSize: '0.88rem' }}>
          No tenés todas las figuritas requeridas para ofertar. Te falta
          {faltantesRequeridas.length === 1 ? '' : 'n'}:{' '}
          <strong>{faltantesRequeridas.map((f) => f.jugador).join(', ')}</strong>.
        </div>
      )}

      {/* Selección de figuritas */}
      {cumpleCalificacion && tieneTodassLasRequeridas && (
        <div className="mt-3 d-flex flex-column gap-3">
          <div>
            <p className="mb-0 fw-semibold" style={{ fontSize: '0.9rem' }}>
              Seleccioná las figuritas que querés ofrecer
            </p>
            {bloqueadas.length > 0 && (
              <p className="mb-0 text-muted" style={{ fontSize: '0.78rem' }}>
                Las figuritas marcadas como <strong>Requerida</strong> se incluyen automáticamente.
                Podés sumar más si querés.
              </p>
            )}
          </div>

          <ScrollRepetidas
            figuritas={repetidas}
            loading={false}
            modo="multiple"
            seleccionadas={figuritasExtra}
            onToggle={toggleExtra}
            bloqueadas={bloqueadas}
          />

          {/* Tags de toda la selección */}
          {todasSeleccionadas.length > 0 && (
            <div className="d-flex flex-wrap gap-1">
              {todasSeleccionadas.map((f) => {
                const esBloqueada = bloqueadas.some((b) => b.figurita_id === f.figurita_id)
                return (
                  <span
                    key={f.figurita_id}
                    className="badge rounded-pill d-flex align-items-center gap-1"
                    style={{
                      backgroundColor: esBloqueada ? '#dbeafe' : '#e1f5ee',
                      color: esBloqueada ? '#1e40af' : '#0f6e56',
                      fontSize: '0.73rem',
                    }}
                  >
                    {f.jugador}
                    {!esBloqueada && (
                      <button
                        className="border-0 bg-transparent p-0"
                        style={{ color: '#0f6e56', lineHeight: 1, cursor: 'pointer' }}
                        onClick={() => toggleExtra(f)}
                      >
                        ×
                      </button>
                    )}
                  </span>
                )
              })}
            </div>
          )}

          <button
            className="btn btn-success px-4 align-self-end"
            disabled={!puedeOfertar}
            onClick={onEnviar}
          >
            Enviar oferta ↗
          </button>
        </div>
      )}
    </div>
  )
}

export default CrearOferta
