import { useEffect, useState } from 'react'
import { crearSubasta } from '../../../services/subastasService.js'
import { useNavigate } from 'react-router'
import useUsuarioActual from '../../../hooks/useUsuarioActual.js'
import ScrollRepetidas from '../../../components/ui/selector-repetidas/scroll-repetidas/scroll-repetidas.jsx'
import ScrollFaltantes from '../../../components/ui/scroll-faltantes/scroll-faltantes.jsx'

const calcularCierreEstimado = (horas) => {
  if (!horas) return null
  const fecha = new Date(Date.now() + horas * 3600000)
  return fecha.toLocaleString('es-AR', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const CALIFICACION_MIN_OPTS = [
  { label: '1+', value: 1 },
  { label: '2+', value: 2 },
  { label: '3+', value: 3 },
  { label: '4+', value: 4 },
  { label: '5', value: 5 },
]

// ─── Paso 1: Elegir figurita a subastar ───────────────────────────────────────
const PasoFigurita = ({ seleccionada, onSeleccionar, userId }) => {
  const [repetidas, setRepetidas] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true)
        //const res = await buscarRepetidas();
        setRepetidas(res ?? [])
      } catch {
        setRepetidas([])
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [userId])

  return (
    <ScrollRepetidas
      figuritas={repetidas}
      loading={loading}
      seleccionada={seleccionada}
      onSeleccionar={onSeleccionar}
    />
  )
}

// ─── Paso 2: Duración ─────────────────────────────────────────────────────────
const PasoDuracion = ({ duracion, onCambiar }) => (
  <div className="d-flex flex-column gap-3">
    <div className="d-flex align-items-center gap-2">
      <input
        type="number"
        min="1"
        max="168"
        className="form-control"
        style={{ maxWidth: '120px' }}
        placeholder="Ej: 6"
        value={duracion ?? ''}
        onChange={(e) => {
          const num = parseInt(e.target.value, 10)
          onCambiar(isNaN(num) || num <= 0 ? null : num)
        }}
      />
      <span className="text-muted" style={{ fontSize: '0.85rem' }}>
        horas
      </span>
    </div>
    {duracion && (
      <p className="mb-0 text-muted" style={{ fontSize: '0.82rem' }}>
        La subasta durará{' '}
        <strong>
          {duracion} {duracion === 1 ? 'hora' : 'horas'}
        </strong>
        .
      </p>
    )}
  </div>
)

// ─── Paso 3: Condiciones para ofertar ────────────────────────────────────────
const PasoCondiciones = ({
  figuritasDeseadas,
  onCambiarFiguritas,
  calificacionMin,
  onCambiarCalificacion,
  userId,
}) => {
  const [figuritasFaltantes, setFiguritasFaltantes] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // buscarFaltantes(userId)
    //   .then((res) => setFiguritasFaltantes(res ?? []))
    //   .finally(() => setLoading(false));
  }, [userId])

  const toggleFigurita = (fig) => {
    const existe = figuritasDeseadas.some((f) => f.id === fig.id)
    onCambiarFiguritas(
      existe ? figuritasDeseadas.filter((f) => f.id !== fig.id) : [...figuritasDeseadas, fig],
    )
  }

  const estrellas = (n, activo) =>
    [...Array(5)].map((_, i) => (
      <span
        key={i}
        style={{
          color: i < n ? (activo ? '#fff' : '#f59e0b') : '#d1d5db',
          fontSize: '0.8rem',
        }}
      >
        ★
      </span>
    ))

  return (
    <div className="d-flex flex-column gap-4">
      {/* Figuritas deseadas */}
      <div className="d-flex flex-column gap-2">
        <div>
          <p className="mb-0 fw-semibold" style={{ fontSize: '0.9rem' }}>
            Figuritas que aceptás como oferta
          </p>
          <p className="mb-0 text-muted" style={{ fontSize: '0.78rem' }}>
            El ofertante debe incluir al menos una de estas figuritas
          </p>
        </div>

        <ScrollFaltantes
          figuritas={figuritasFaltantes}
          loading={loading}
          seleccionadas={figuritasDeseadas}
          onToggle={toggleFigurita}
        />

        {/* Tags de seleccionadas */}
        {figuritasDeseadas.length === 0 ? (
          <p className="mb-0 text-muted fst-italic" style={{ fontSize: '0.78rem' }}>
            Sin restricción — aceptás cualquier figurita
          </p>
        ) : (
          <div className="d-flex flex-wrap gap-1">
            {figuritasDeseadas.map((f) => (
              <span
                key={f.id}
                className="badge rounded-pill d-flex align-items-center gap-1"
                style={{ backgroundColor: '#e1f5ee', color: '#0f6e56', fontSize: '0.73rem' }}
              >
                {f.jugador}
                <button
                  className="border-0 bg-transparent p-0"
                  style={{ color: '#0f6e56', lineHeight: 1, cursor: 'pointer' }}
                  onClick={() => toggleFigurita(f)}
                >
                  ×
                </button>
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Calificación mínima */}
      <div className="d-flex flex-column gap-2">
        <div>
          <p className="mb-0 fw-semibold" style={{ fontSize: '0.9rem' }}>
            Calificación mínima del ofertante
          </p>
          <p className="mb-0 text-muted" style={{ fontSize: '0.78rem' }}>
            Solo usuarios con esta calificación o más podrán ofertar
          </p>
        </div>

        <div className="d-flex flex-wrap gap-2">
          {CALIFICACION_MIN_OPTS.map(({ label, value }) => {
            const activo = calificacionMin === value
            return (
              <button
                key={value}
                className={`btn btn-sm rounded-pill px-3 d-flex align-items-center gap-1 ${
                  activo ? 'btn-success text-white' : 'btn-outline-secondary'
                }`}
                style={{ fontSize: '0.82rem' }}
                onClick={() => onCambiarCalificacion(value)}
              >
                {estrellas(value, activo)}
                <span className="ms-1">{label}</span>
              </button>
            )
          })}
        </div>

        <p className="mb-0 text-muted" style={{ fontSize: '0.78rem' }}>
          {calificacionMin === 1
            ? 'Cualquier usuario puede ofertar en tu subasta'
            : `Solo usuarios con ${calificacionMin}★ o más pueden ofertar`}
        </p>
      </div>
    </div>
  )
}

// ─── Resumen ──────────────────────────────────────────────────────────────────
const Resumen = ({ figurita, duracion, figuritasDeseadas, calificacionMin }) => {
  const cierre = calcularCierreEstimado(duracion)

  return (
    <div className="border rounded-3 overflow-hidden" style={{ borderColor: '#9FE1CB' }}>
      <div
        className="px-3 py-2 border-bottom"
        style={{ backgroundColor: '#f0f9f4', borderColor: '#9FE1CB' }}
      >
        <span
          className="fw-bold text-uppercase text-muted"
          style={{ fontSize: '0.72rem', letterSpacing: '0.08em' }}
        >
          Resumen de la subasta
        </span>
      </div>
      <div className="px-3 py-1">
        {[
          {
            label: 'Figurita',
            valor: figurita ? (
              <span className="fw-semibold">{figurita.jugador}</span>
            ) : (
              <span className="text-muted fst-italic">Sin seleccionar</span>
            ),
          },
          {
            label: 'Duración',
            valor: duracion ? (
              <span className="fw-semibold">
                {duracion} {duracion === 1 ? 'hora' : 'horas'}
              </span>
            ) : (
              <span className="text-muted">—</span>
            ),
          },
          {
            label: 'Cierre estimado',
            valor: cierre ? (
              <span className="fw-semibold">{cierre}</span>
            ) : (
              <span className="text-muted">—</span>
            ),
          },
          {
            label: 'Figuritas deseadas',
            valor:
              figuritasDeseadas.length > 0 ? (
                <span className="fw-semibold">
                  {figuritasDeseadas.map((f) => f.jugador).join(', ')}
                </span>
              ) : (
                <span className="text-muted">Sin restricción</span>
              ),
          },
          {
            label: 'Calificación mínima',
            valor:
              calificacionMin === 1 ? (
                <span className="text-muted">Sin mínimo</span>
              ) : (
                <span className="fw-semibold">{calificacionMin}★ o más</span>
              ),
          },
        ].map(({ label, valor }) => (
          <div
            key={label}
            className="d-flex justify-content-between align-items-center py-2 border-bottom"
            style={{ fontSize: '0.85rem', borderColor: '#e8f5f0' }}
          >
            <span className="text-muted">{label}</span>
            <span>{valor}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

// ─── Vista principal ──────────────────────────────────────────────────────────
const CrearSubasta = () => {
  const navigate = useNavigate()
  const { userId } = useUsuarioActual()

  const [figurita, setFigurita] = useState(null)
  const [duracion, setDuracion] = useState(6)
  const [figuritasDeseadas, setFiguritasDeseadas] = useState([])
  const [calificacionMin, setCalificacionMin] = useState(1)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const puedePublicar = figurita !== null && duracion !== null

  const handlePublicar = async () => {
    if (!puedePublicar) return
    try {
      setLoading(true)
      setError(null)
      await crearSubasta(userId, {
        figurita_id: figurita.figurita_id,
        duracion_en_horas: duracion,
        figuritas_deseadas_ids: figuritasDeseadas.map((f) => f.id),
        calificacion_minima: calificacionMin,
        user_id: userId,
      })
      navigate('/subastas')
    } catch {
      setError('Ocurrió un error al publicar la subasta. Intentá de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  const PASOS = [
    { numero: 1, label: 'Elegí la figurita a subastar' },
    { numero: 2, label: 'Duración' },
    { numero: 3, label: 'Condiciones para ofertar' },
  ]

  return (
    <main className="container py-4 px-3 px-md-4">
      <div className="mx-auto d-flex flex-column gap-4" style={{ maxWidth: '680px' }}>
        {/* Header */}
        <div>
          <h1 className="fw-bold mb-1" style={{ fontSize: '1.4rem' }}>
            Crear subasta
          </h1>
          <p className="text-muted mb-0" style={{ fontSize: '0.88rem' }}>
            Completá los pasos para publicar tu figurita
          </p>
        </div>

        {/* Pasos */}
        {PASOS.map(({ numero, label }) => (
          <div key={numero} className="d-flex flex-column gap-3">
            <div className="d-flex align-items-center gap-2">
              <div
                className="d-flex align-items-center justify-content-center rounded-circle flex-shrink-0"
                style={{
                  width: 26,
                  height: 26,
                  backgroundColor: '#0f6e56',
                  color: '#fff',
                  fontSize: '0.75rem',
                  fontWeight: 700,
                }}
              >
                {numero}
              </div>
              <span
                className="fw-bold text-uppercase text-muted"
                style={{ fontSize: '0.78rem', letterSpacing: '0.08em' }}
              >
                {label}
              </span>
            </div>

            <div className="ms-1">
              {numero === 1 && (
                <PasoFigurita seleccionada={figurita} onSeleccionar={setFigurita} userId={userId} />
              )}
              {numero === 2 && <PasoDuracion duracion={duracion} onCambiar={setDuracion} />}
              {numero === 3 && (
                <PasoCondiciones
                  figuritasDeseadas={figuritasDeseadas}
                  onCambiarFiguritas={setFiguritasDeseadas}
                  calificacionMin={calificacionMin}
                  onCambiarCalificacion={setCalificacionMin}
                  userId={userId}
                />
              )}
            </div>

            {numero < PASOS.length && <hr className="my-1" />}
          </div>
        ))}

        {/* Resumen */}
        <Resumen
          figurita={figurita}
          duracion={duracion}
          figuritasDeseadas={figuritasDeseadas}
          calificacionMin={calificacionMin}
        />

        {/* Error */}
        {error && (
          <div className="alert alert-danger py-2" style={{ fontSize: '0.85rem' }}>
            {error}
          </div>
        )}

        {/* Acciones */}
        <div className="d-flex gap-2 justify-content-center pb-4">
          <button
            className="btn btn-outline-secondary px-4"
            style={{ fontSize: '0.9rem' }}
            onClick={() => navigate(-1)}
          >
            Cancelar
          </button>
          <button
            className="btn btn-success px-4"
            style={{ fontSize: '0.9rem' }}
            disabled={!puedePublicar || loading}
            onClick={handlePublicar}
          >
            {loading ? 'Publicando...' : 'Publicar subasta ↗'}
          </button>
        </div>
      </div>
    </main>
  )
}

export default CrearSubasta
