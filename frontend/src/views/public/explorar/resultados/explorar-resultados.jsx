import FiguritaCard from '@/components/ui/figurita-card/figurita-card'
import FiguritaCardSkeleton from '@/components/ui/figurita-card/figurita-card-skeleton'
import Pagination from '@/components/ui/pagination/pagination'
import styles from './explorar-resultados.module.css'

const ExplorarResultados = ({
  figuritas, totalElements, totalPages, page, onPageChange, loading, error,
}) => {
  if (error) return <p className="text-danger text-center py-4">No se pudieron cargar las figuritas</p>

  return (
    <>
      <p className={styles.resultsCount}>{totalElements} figuritas encontradas</p>

      <div className="row g-3">
        {loading
          ? [...Array(12)].map((_, i) => (
            <div key={i} className="col-12 col-md-6 col-lg-4">
              <FiguritaCardSkeleton />
            </div>
          ))
          : figuritas.map(fig => (
            <div key={fig.id} className="col-12 col-md-6 col-lg-4">
              <FiguritaCard
                id={fig.id}
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
                figurita={fig}
              />
            </div>
          ))
        }
      </div>

      <Pagination page={page} totalPages={totalPages} onPageChange={onPageChange} />
    </>
  )
}

export default ExplorarResultados
