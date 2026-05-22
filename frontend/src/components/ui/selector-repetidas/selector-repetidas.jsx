import { useState, useEffect, useCallback } from 'react'
import { buscarRepetidas } from '@/services/coleccionService.js'
import ScrollFiguritas from './scroll-figuritas/scroll-figuritas.jsx'
import styles from './selector-repetidas.module.css'

const LIMITE = 10

const SelectorRepetidas = ({ modo = 'unica', bloqueadas = [], onChange }) => {
  const [figuritas, setFiguritas] = useState([])
  const [loading, setLoading] = useState(false)
  const [seleccionadas, setSeleccionadas] = useState([])
  const [total, setTotal] = useState(null)

  const fetchRepetidas = useCallback(async (busqueda = '') => {
    setLoading(true)
    try {
      const payload = await buscarRepetidas({ jugador: busqueda, pagina: 1, limite: LIMITE })
      setFiguritas(payload.contenido ?? [])
      setTotal(payload.cantidadDeElementos ?? null)
    } catch (e) {
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchRepetidas()
  }, [fetchRepetidas])

  const toggle = (fig) => {
    const esBloqueada = bloqueadas.some((b) => b.figuritaId === fig.figuritaId)
    if (esBloqueada) return

    setSeleccionadas((prev) => {
      const existe = prev.some((f) => f.figuritaId === fig.figuritaId)
      const next =
        modo === 'unica'
          ? existe
            ? []
            : [fig]
          : existe
            ? prev.filter((f) => f.figuritaId !== fig.figuritaId)
            : [...prev, fig]
      onChange?.(next)
      return next
    })
  }

  const todasVisibles = [...bloqueadas, ...seleccionadas]

  return (
    <div className="d-flex flex-column gap-3">
      <ScrollFiguritas
        figuritas={figuritas}
        loading={loading}
        totalDisponibles={total}
        onBuscar={fetchRepetidas}
        modo={modo}
        seleccionadas={seleccionadas}
        onToggle={toggle}
        bloqueadas={bloqueadas}
        titulo="Tus repetidas"
        placeholder="Buscar en tus repetidas..."
      />

      {todasVisibles.length > 0 && (
        <div className={styles['selector-tags']}>
          {todasVisibles.map((f) => {
            const esBloqueada = bloqueadas.some((b) => b.figuritaId === f.figuritaId)
            return (
              <span
                key={f.figuritaId}
                className={`${styles['selector-tag']} ${esBloqueada ? styles['bloqueada'] : styles['opcional']}`}
              >
                {f.jugador}
                {!esBloqueada && (
                  <button className={styles['selector-tag-quitar']} onClick={() => toggle(f)}>
                    ×
                  </button>
                )}
              </span>
            )
          })}
        </div>
      )}
    </div>
  )
}

export default SelectorRepetidas
