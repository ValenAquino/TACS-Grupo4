import './barra-tiempo.module.css'

const BarraTiempo = ({ finalizada, tiempoRestante, finalizadaHace, derecha }) => (
  <div className="barra-tiempo px-3 py-2 d-flex justify-content-between align-items-center">
    <span className="text-muted">
      {finalizada ? (
        <>
          ● Finalizada hace <strong>{finalizadaHace}</strong>
        </>
      ) : (
        <>
          ● Tiempo restante <strong>{tiempoRestante}</strong>
        </>
      )}
    </span>
    {derecha && <span className="text-muted">{derecha}</span>}
  </div>
)

export default BarraTiempo
