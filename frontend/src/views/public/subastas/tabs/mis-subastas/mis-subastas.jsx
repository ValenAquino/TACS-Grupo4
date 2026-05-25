import { useEffect, useState } from 'react'
import { buscarSubastas } from '../../../../../services/subastasService.js'
import MiSubasta from './mi-subasta/mi-subasta.jsx'
import Button from '../../../../../components/ui/button/button.jsx'
import FilterChip from '../../../../../components/ui/filter-chip/filter-chip.jsx'
import Paginacion from '../../../../../components/ui/paginacion/paginacion.jsx'
import { useNavigate } from 'react-router'
import { useAuth } from '@/contexts/userContext.jsx'
import { useError } from '@/contexts/errorContext.jsx'

const MisSubastas = () => {
  const [data, setData] = useState({})
  const [loading, setLoading] = useState(true)
  const [estado, setEstado] = useState('ACTIVA')
  const [pagina, setPagina] = useState(1)
  const [refresh, setRefresh] = useState(0)

  const navigate = useNavigate()
  const { user } = useAuth()
  const { handleError } = useError()

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true)
        const res = await buscarSubastas({ autorId: user.perfil_id, estado, pagina, limite: 5 })
        setData(res)
      } catch (error) {
        handleError(error, () => {})
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [estado, pagina, refresh])

  const cambiarEstado = (nuevoEstado) => {
    if (estado === nuevoEstado) return
    setEstado(nuevoEstado)
    setPagina(1)
  }

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      <div className="d-flex justify-content-end">
        <Button label="Crear subasta ↗" onClick={() => navigate('/subastas/crear')} />
      </div>

      <div className="d-flex gap-2">
        <FilterChip
          label="Activas"
          selected={estado === 'ACTIVA'}
          onClick={() => cambiarEstado('ACTIVA')}
        />
        <FilterChip
          label="Finalizadas"
          selected={estado === 'FINALIZADA'}
          onClick={() => cambiarEstado('FINALIZADA')}
        />
      </div>

      {loading ? (
        <div className="d-flex flex-column gap-3">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="rounded-3 placeholder-glow border" style={{ height: '180px' }}>
              <div className="placeholder w-100 h-100 rounded-3" />
            </div>
          ))}
        </div>
      ) : data.contenido?.length > 0 ? (
        <>
          <div className="d-flex flex-column gap-3">
            {data.contenido.map((sub) => (
              <MiSubasta
                key={sub.id}
                subasta={sub}
                finalizada={estado === 'FINALIZADA'}
                onRefresh={() => setRefresh((r) => r + 1)}
              />
            ))}
          </div>
          <div className="pt-3 d-flex justify-content-center">
            <Paginacion
              page={pagina}
              totalPages={data.cantidad_de_paginas ?? 1}
              onChange={setPagina}
            />
          </div>
        </>
      ) : (
        <div className="text-center text-muted py-5">
          <div className="fs-1">📭</div>
          <p className="mb-0">
            {estado === 'ACTIVA' ? 'No tenés subastas activas' : 'No tenés subastas finalizadas'}
          </p>
        </div>
      )}
    </div>
  )
}

export default MisSubastas
