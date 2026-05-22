import Indicador from './indicador/indicador'
import FiguritaInfo from './figurita-info/figurita-info'
import InsigniaFila from './insignia-fila/insignia-fila'
import styles from './fila-scroll.module.css'

const FilaScroll = ({ fig, modo, bloqueada, seleccionada, onToggle }) => {
  const disponibles = fig.cantidadExistente - fig.cantidadReservada - (seleccionada ? 1 : 0)
  const sinStock = disponibles <= 0
  const bloqueadaSinStock = bloqueada && sinStock
  const deshabilitado = bloqueada || sinStock

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
      <InsigniaFila
        fig={fig}
        bloqueada={bloqueada}
        bloqueadaSinStock={bloqueadaSinStock}
        seleccionada={seleccionada}
        sinStock={sinStock}
        disponibles={disponibles}
      />
    </button>
  )
}

export default FilaScroll