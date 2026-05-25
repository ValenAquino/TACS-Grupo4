import { useState, useEffect } from 'react'
import IntercambioCard from '../../../../components/ui/intercambio-card/intercambio-card.jsx'
import IntercambioModal from '../../../../components/ui/intercambio-modal/intercambio-modal.jsx'
import { aceptarIntercambio, rechazarIntercambio } from '../../../../services/intercambioService.js'
import { buscarPropuestas } from '@/services/propuestasService.js'
import Paginacion from '@/components/ui/paginacion/paginacion.jsx'
import { useError } from '@/contexts/errorContext.jsx'
import FilterChip from '@/components/ui/filter-chip/filter-chip.jsx'
import Button from '@/components/ui/button/button.jsx'

const RecibidasTab = () => {
  const [selected, setSelected] = useState(null)
  const [loading, setLoading] = useState(true)
  const [pagina, setPagina] = useState(1)
  const [recibidas, setRecibidas] = useState([])
  const [filtros, setFiltros] = useState({
    estado: '',
    tipo: 'RECIBIDAS',
  })

  const { handleError } = useError()

  const cargarRecibidas = async () => {
    try {
      setLoading(true)
      const res = await buscarPropuestas({ pagina, limite: 10, ...filtros })
      setRecibidas(res)
    } catch (error) {
      handleError(error, () => {})
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    cargarRecibidas()
  }, [pagina, filtros])

  const handleAceptar = async (id) => {
    await aceptarIntercambio(id)
    setSelected(null)
    cargarRecibidas()
  }

  const handleRechazar = async (id) => {
    await rechazarIntercambio(id)
    setSelected(null)
    cargarRecibidas()
  }

  const cambiarFiltro = (nuevoEstado) => {
    setFiltros((prev) => {
      if (prev.estado === nuevoEstado) return prev
      return { ...prev, estado: nuevoEstado }
    })
    setPagina(1)
  }

  const textosEstado = {
    '': 'Todas las propuestas',
    PENDIENTE: 'Esperando respuesta',
    ACEPTADO: 'Propuestas aceptadas',
    RECHAZADO: 'Propuestas rechazadas',
    CANCELADO: 'Propuestas canceladas',
  }

  const textoResultados = textosEstado[filtros.estado] || 'Propuestas'

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      <div className="d-flex flex-wrap gap-2">
        <FilterChip
          label="Todas"
          selected={filtros.estado === ''}
          onClick={() => cambiarFiltro('')}
        />
        <FilterChip
          label="Aceptadas"
          selected={filtros.estado === 'ACEPTADO'}
          onClick={() => cambiarFiltro('ACEPTADO')}
        />
        <FilterChip
          label="Rechazadas"
          selected={filtros.estado === 'RECHAZADO'}
          onClick={() => cambiarFiltro('RECHAZADO')}
        />
        <FilterChip
          label="Pendientes"
          selected={filtros.estado === 'PENDIENTE'}
          onClick={() => cambiarFiltro('PENDIENTE')}
        />
        <FilterChip
          label="Canceladas"
          selected={filtros.estado === 'CANCELADO'}
          onClick={() => cambiarFiltro('CANCELADO')}
        />
      </div>

      {loading ? (
        <div className="d-flex flex-column gap-3">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="rounded-3 placeholder-glow border" style={{ height: '140px' }}>
              <div className="placeholder w-100 h-100 rounded-3" />
            </div>
          ))}
        </div>
      ) : recibidas?.contenido?.length > 0 ? (
        <>
          <p className="mb-0">
            {textoResultados} ({recibidas.cantidad_de_elementos})
          </p>
          <div className="d-flex flex-column gap-3">
            {recibidas.contenido.map((i) => (
              <IntercambioCard
                key={i.id}
                intercambio={i}
                tipo="RECIBIDA"
                onActualizado={cargarRecibidas}
              />
            ))}
          </div>
          <div className="pt-3 d-flex justify-content-center">
            <Paginacion
              page={pagina}
              totalPages={recibidas.cantidad_de_paginas ?? 1}
              onChange={setPagina}
            />
          </div>
        </>
      ) : (
        <div className="text-center text-muted py-5">
          <div className="fs-1">📭</div>
          <p className="mb-0">No hay resultados...</p>
        </div>
      )}

      <IntercambioModal
        selected={selected}
        onClose={() => setSelected(null)}
        izquierda="pide"
        derecha="ofrece"
        labelIzq="Te pide"
        labelDer="Te ofrece"
        extraBotones={
          <>
            <Button
              label="✓ Aceptar"
              variante="primario"
              className="flex-fill"
              onClick={() => handleAceptar(selected.id)}
            />
            <Button
              label="✗ Rechazar"
              variante="peligroBorde"
              className="flex-fill"
              onClick={() => handleRechazar(selected.id)}
            />
          </>
        }
      />
    </div>
  )
}

export default RecibidasTab
