import Paginacion from '@/components/ui/paginacion/paginacion.jsx'
import estrellas from '@/components/ui/estrellas/estrellas.jsx'
import CalificacionSkeleton from './CalificacionSkeleton.jsx'
import styles from './PerfilCalificaciones.module.css'

const PerfilCalificaciones = ({ reviews, loadingNotificaciones, pagina, onPaginaChange }) => {
  return (
    <div className={`mx-auto mt-4 px-3 ${styles.wrapper}`}>
      <div className="mb-4">
        <h5 className="mb-3 fw-bold">Últimas calificaciones</h5>

        <div className="d-flex flex-column gap-3">
          {loadingNotificaciones ? (
            [...Array(10)].map((_, i) => <CalificacionSkeleton key={i} />)
          ) : reviews.contenido?.length === 0 ? (
            <p className="text-muted">Este usuario no tiene reseñas disponibles</p>
          ) : (
            reviews.contenido?.map((r, i) => (
              <div
                key={i}
                className="p-3 bg-white rounded shadow-sm d-flex align-items-center gap-3"
              >
                <div className={styles['avatar-reviewer']}>{r.iniciales}</div>

                <div className="flex-grow-1">
                  <strong>{r.nombre}</strong>
                  <div>
                    {estrellas(r.valor)} {r.valor}/5
                  </div>
                  <p className={`mb-0 text-muted ${styles.descripcion}`}>{r.descripcion}</p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
      <Paginacion
        page={pagina}
        totalPages={reviews.cantidad_de_paginas ?? 1}
        onChange={onPaginaChange}
      />
    </div>
  )
}

export default PerfilCalificaciones
