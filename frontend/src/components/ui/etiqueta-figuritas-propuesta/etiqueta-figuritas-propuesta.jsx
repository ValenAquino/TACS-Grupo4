import Etiqueta from '../etiqueta/etiqueta.jsx'

export const EtiquetaFiguritasOfrecidas = ({ propuesta }) => (
  <div className="d-flex flex-wrap gap-1">
    {propuesta.figuritas_ofrecidas.map((fig, index) => (
      <Etiqueta key={index} label={`${fig.jugador} #${fig.numero}`} variante="secundario" />
    ))}
  </div>
)
export default EtiquetaFiguritasOfrecidas
