import styles from './insignia-fila.module.css'

const InsigniaFila = ({ fig, bloqueada, bloqueadaSinStock, seleccionada, sinStock, disponibles }) => (
  <div className={styles['scroll-badges']}>
    {bloqueada && (
      <span className={styles['scroll-badge'] + ' ' + (bloqueadaSinStock ? styles['advertencia'] : styles['requerida'])}>
        {bloqueadaSinStock ? "Sin stock" : "Requerida"}
      </span>
    )}
    <span className={styles['scroll-badge'] + ' ' + (sinStock ? styles['sin-stock-badge'] : styles['cantidad'])}>
      {sinStock ? "Sin stock" : (
        <>
          ×{disponibles}
          {(fig.cantidadReservada > 0 || seleccionada) && (
            <span className={styles['scroll-badge-total']}> de {fig.cantidadExistente}</span>
          )}
        </>
      )}
    </span>
  </div>
);

export default InsigniaFila