import Button from '@/components/ui/button/button.jsx'
import renderStars from '@/utils/renderStars.jsx'
import styles from './PerfilHeader.module.css'

const PerfilHeader = ({
  perfil,
  loading,
  promedio,
  reviews,
  perfilId,
  onEditarClick,
  onCerrarSesionClick,
}) => {
  return (
    <div className={styles.wrapper}>
      <div
        className={`mx-auto d-flex justify-content-between align-items-center px-4 py-4 ${styles.inner}`}
      >
        <div className="d-flex align-items-center gap-3 text-white">
          <div className={styles.avatar}>MG</div>

          <div>
            {loading ? (
              <h2>Cargando datos...</h2>
            ) : (
              <>
                <h2 className={`mb-0 fw-bold ${styles.nombre}`}>{perfil?.nombre}</h2>
                <div>
                  {renderStars(Number(promedio))} ⭐ {promedio} ({reviews.cantidad_de_elementos})
                </div>
              </>
            )}
          </div>
        </div>

        <div className="d-flex align-items-center gap-3">
          <button className={`btn btn-warning ${styles['btn-editar']}`} onClick={onEditarClick}>
            Editar perfil
          </button>

          {perfilId != null && <Button label={'Cerrar sesion'} onClick={onCerrarSesionClick} />}
        </div>
      </div>
    </div>
  )
}

export default PerfilHeader
