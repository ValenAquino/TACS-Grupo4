import { useState } from 'react'

const usePaginacion = () => {
  const [pagina, setPagina] = useState(1)

  const resetPagina = () => setPagina(1)

  return { pagina, setPagina, resetPagina }
}

export default usePaginacion
