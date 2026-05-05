import FiguritaCard from '../../../../components/ui/figurita-card/figurita-card'
import FiguritaCardSkeleton from '../../../../components/ui/figurita-card/figurita-card-skeleton'
import Pagination from '../../../../components/ui/pagination/pagination'
import styles from './explorar-resultados.module.css'

const ORDENAR_OPTS = [
  { key: 'numero', label: 'Número' },
  { key: 'reputacion', label: 'Reputación' },
]

const ExplorarResultados = ({
  figuritas,
  totalElements,
  totalPages,
  page,
  onPageChange,
  ordenar,
  onOrdenarChange,
  onAction,
  loading,
  error,
}) => {
  if (error)
    return <p className="text-danger text-center py-4">No se pudieron cargar las figuritas</p>

  return (
    <>
      {/* Contador y ordenamiento */}
      <div className={styles.resultsHeader}>
        <p className={styles.resultsCount}>{totalElements} figuritas encontradas</p>
        <div className={styles.sortBtns}>
          <span className={styles.sortLabel}>Ordenar:</span>
          {ORDENAR_OPTS.map(({ key, label }) => (
            <button
              key={key}
              className={`${styles.sortBtn} ${ordenar === key ? styles.sortBtnActive : ''}`}
              onClick={() => onOrdenarChange(key)}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* Grid de cards */}
      <div className="row g-3">
        {loading
          ? [...Array(12)].map((_, i) => (
              <div key={i} className="col-12 col-md-6 col-lg-4">
                <FiguritaCardSkeleton />
              </div>
            ))
          : figuritas.map((fig) => (
              <div key={fig.id} className="col-12 col-md-6 col-lg-4">
                <FiguritaCard
                  number={fig.number}
                  type={fig.type}
                  imageUrl={fig.imageUrl}
                  emoji={fig.emoji}
                  emojiBg={fig.emojiBg}
                  name={fig.name}
                  subtitle={fig.subtitle}
                  available={fig.available}
                  extra={fig.extra}
                  user={fig.user}
                  onAction={() => onAction(fig)}
                />
              </div>
            ))}
      </div>

      <Pagination page={page} totalPages={totalPages} onPageChange={onPageChange} />
    </>
  )
}

export default ExplorarResultados
