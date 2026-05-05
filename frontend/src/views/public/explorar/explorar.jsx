import { useEffect, useRef, useState } from 'react'
import useFiguritas from '@/hooks/useFiguritas'
import ExplorarSearch from './search/explorar-search'
import SugerenciasBanner from './sugerencias-banner/sugerencias-banner'
import ExplorarFiltros from './filtros/explorar-filtros'
import ExplorarResultados from './resultados/explorar-resultados'
import styles from './explorar.module.css'

const FILTROS_INICIAL = { tipo: 'todos', jugador: '', seleccion: '', numero: '' }

const Explorar = () => {
  const resultadosRef = useRef(null)
  const [filtros, setFiltros] = useState(FILTROS_INICIAL)
  const [page, setPage] = useState(0)

  const { figuritas, totalPages, totalElements, loading, error } = useFiguritas(
    filtros.jugador,
    filtros.seleccion,
    filtros.numero,
    filtros.tipo,
    page,
  )

  const handleAplicar = (nuevosFiltros) => {
    setFiltros(nuevosFiltros)
    setPage(0)
  }

  useEffect(() => {
    resultadosRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [page])

  return (
    <main className={styles.page}>
      <ExplorarSearch onQueryChange={(jugador) => handleAplicar({ ...filtros, jugador })} />
      <div className={styles.container}>
        <SugerenciasBanner />
        <ExplorarFiltros onAplicar={handleAplicar} />
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
