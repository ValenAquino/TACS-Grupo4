export const EtiquetaPropuesta = ({ propuesta }) => (
  <>
    {propuesta.figuritas_ofrecidas.map((fig, index) => (
      <span key={index} className={styles.figuritaOfrecida}>
        {index > 0 && ' + '}
        {fig.jugador} #{fig.numero}
      </span>
    ))}
  </>
)

export default EtiquetaPropuesta;
