import { useEffect, useRef, useState } from 'react'
import useFiguritas from '@/hooks/useFiguritas'
import ExplorarSearch from './search/explorar-search'
import SugerenciasBanner from './sugerencias-banner/sugerencias-banner'
import ExplorarFiltros from './filtros/explorar-filtros'
import ExplorarResultados from './resultados/explorar-resultados'
import styles from './explorar.module.css'

const FILTROS_INICIAL = { q: '', tipo: 'todos', jugador: '', seleccion: '', numero: '' }

const Explorar = () => {
  const resultadosRef = useRef(null)
  const heroInputRef = useRef(null)
  const [filtros, setFiltros] = useState(FILTROS_INICIAL)
  const [page, setPage] = useState(1)

  const { figuritas, totalPages, totalElements, loading, error } = useFiguritas(
    filtros.q,
    filtros.jugador,
    filtros.seleccion,
    filtros.numero,
    filtros.tipo,
    page,
  )

  const handleAplicar = (nuevosFiltros) => {
    setFiltros(nuevosFiltros)
    setPage(1)
  }

  useEffect(() => {
    resultadosRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [page])

  return (
    <main className={styles.page}>
      <ExplorarSearch ref={heroInputRef} onQueryChange={(q) => handleAplicar({ ...FILTROS_INICIAL, q })} />
      <div className={styles.container}>
        <SugerenciasBanner />
        <ExplorarFiltros
          onAplicar={(panelFiltros) => {
            if (heroInputRef.current) heroInputRef.current.value = ''
            handleAplicar({ ...FILTROS_INICIAL, ...panelFiltros })
          }}
        />
        <div ref={resultadosRef} />
        <ExplorarResultados
          figuritas={figuritas}
          totalElements={totalElements}
          totalPages={totalPages}
          page={page}
          onPageChange={setPage}
          loading={loading}
          error={error}
        />
      </div>
    </main>
  )
}

export default Explorar
