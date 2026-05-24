import Indicador from './indicador/indicador'
import FiguritaInfo from './figurita-info/figurita-info'
import InsigniaFila from './insignia-fila/insignia-fila'
import styles from './fila-scroll.module.css'

const FilaScroll = ({ fig, modo, bloqueada, seleccionada, onToggle, eraInicial = false }) => {
  const tieneCantidad = fig.cantidad_existente !== undefined
  const reservaEfectiva = tieneCantidad ? fig.cantidad_reservada - (eraInicial ? 1 : 0) : 0
  const disponibles = tieneCantidad
    ? fig.cantidad_existente - reservaEfectiva - (seleccionada ? 1 : 0)
    : null
  const sinStock = tieneCantidad ? disponibles <= 0 : false
  const bloqueadaSinStock = bloqueada && sinStock && !eraInicial
  const deshabilitado = bloqueada || (sinStock && !seleccionada)

  console.log(fig.jugador, {
    cantidad_existente: fig.cantidad_existente,
    cantidad_reservada: fig.cantidad_reservada,
    seleccionada,
    eraInicial,
  })

  const filaClass = [
    styles['scroll-fila'],
    bloqueadaSinStock
      ? styles['bloqueada-sin-stock']
      : seleccionada
        ? styles['seleccionada']
        : sinStock
          ? styles['sin-stock']
          : '',
  ].join(' ')

  return (
    <button
      className={filaClass}
      onClick={() => !deshabilitado && onToggle(fig)}
      disabled={deshabilitado}
    >
      <Indicador
        modo={modo}
        seleccionada={seleccionada}
        sinStock={sinStock}
        bloqueadaSinStock={bloqueadaSinStock}
      />
      <FiguritaInfo fig={fig} opaco={sinStock && !bloqueada} />
      {tieneCantidad && (
        <InsigniaFila
          fig={fig}
          bloqueada={bloqueada}
          bloqueadaSinStock={bloqueadaSinStock}
          seleccionada={seleccionada}
          sinStock={sinStock}
          disponibles={disponibles}
        />
      )}
    </button>
  )
}

export default FilaScroll
