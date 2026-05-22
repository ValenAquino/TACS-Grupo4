import styles from './indicador.module.css'

const Indicador = ({ modo, seleccionada, sinStock, bloqueadaSinStock }) => {
  const indicadorClass = [
    styles['scroll-indicador'],
    modo === 'unica' ? styles['radio'] : styles['checkbox'],
    bloqueadaSinStock
      ? styles['bloqueada-sin-stock']
      : seleccionada
        ? styles['seleccionado']
        : sinStock
          ? 'sin-stock'
          : '',
  ].join(' ')

  if (modo === 'unica') {
    return (
      <div className={indicadorClass}>
        {seleccionada && <div className="scroll-indicador-radio-dot" />}
      </div>
    )
  }

  return (
    <div className={indicadorClass}>
      {seleccionada && !bloqueadaSinStock && <span className="scroll-indicador-check">✓</span>}
      {bloqueadaSinStock && <span className="scroll-indicador-check">!</span>}
    </div>
  )
}

export default Indicador
