import PerfilSimple from '@/components/ui/perfil-simple/perfil-simple.jsx'
import FiguritaRecomendadaCard from '@/views/public/sugerencias/figurita-recomendada-card.jsx'
import styles from '@/views/public/sugerencias/sugerencia-card.module.css'
import { useState } from 'react'

const toggleFavoritoSugerencia = async (sugerenciaId, esFavorito) => {
  // TODO: llamada a la API
  // await api.patch(`/sugerencias/${sugerenciaId}/favorito`, { favorito: esFavorito })
}

const SugerenciaResumen = ({id, figuritasNecesarias, figuritasRecomendadas, perfil, favorito }) => {
  const [esFavorito, setEsFavorito] = useState(favorito)

  const handleToggleFavorito = async () => {
    const nuevoEstado = !esFavorito
    setEsFavorito(!nuevoEstado)
    await toggleFavoritoSugerencia(id)
  }

  return (
    <>
      <div className="d-flex align-items-center justify-content-between">
        <PerfilSimple perfil={perfil} />
        <button
          onClick={handleToggleFavorito}
          className={styles.botonFavorito}
          aria-label={esFavorito ? 'Quitar de favoritos' : 'Agregar a favoritos'}
        >
          {esFavorito ? '★' : '☆'}
        </button>
      </div>

      <hr className="my-3" />

      <div className="d-flex flex-column justify-content-between">
        <div className="d-flex flex-row justify-content-between gap-2">
          <div className="small text-muted mb-1">A ÉL/ELLA LE INTERESA</div>
          <div className="small text-muted mb-1">ÉL/ELLA TIENE</div>
        </div>
        <div className="d-flex flex-row align-items-center gap-2">
          <div className="flex-grow-1">
            {figuritasNecesarias.map((fig) => (
              <FiguritaRecomendadaCard fig={fig} key={fig.id} />
            ))}
          </div>

          <div className={styles.swapIcon}>⇄</div>

          <div className="flex-grow-1 text-end">
            {figuritasRecomendadas.map((fig) => (
              <FiguritaRecomendadaCard fig={fig} key={fig.id} verde={false} />
            ))}
          </div>
        </div>
      </div>
    </>
  )
}

export default SugerenciaResumen