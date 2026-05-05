const ChipFigurita = ({ figurita }) => (
    <div className="border rounded p-2 mb-1 d-flex align-items-center gap-2">
        <small className="text-muted">#{figurita.numero}</small>
        <span>{figurita.nombre}</span>
        {figurita.pais && <small className="text-muted">· {figurita.pais}</small>}
    </div>
);

const BadgeEstado = ({ etiqueta, color }) => (
    <span className={`badge text-bg-${color} ms-2`} style={{
        fontSize: "0.85rem",
        padding: "6px 10px",
        borderRadius: "8px"
    }}>
        {etiqueta}
    </span>
);

const IntercambioCard = ({
    intercambio,
    izquierda,
    derecha,
    labelIzq,
    labelDer,
    badge,
    botones
}) => {

    const izq = intercambio[izquierda] || [];
    const der = intercambio[derecha] || [];

    return (
        <div className="card mb-3 border-success border-2">
            <div className="card-body">

                <div className="d-flex justify-content-between">
                    <strong>{intercambio.usuario.nombre}</strong>
                    {badge && <BadgeEstado {...badge} />}
                </div>

                <div className="row mt-2">
                    <div className="col">
                        <small className="text-uppercase text-muted fw-semibold">
                            {labelIzq}
                        </small>

                        {izq.slice(0, 3).map((f, i) => (
                            <ChipFigurita key={i} figurita={f} />
                        ))}

                        {izq.length > 3 && (
                            <small className="text-muted">
                                +{izq.length - 3} más
                            </small>
                        )}
                    </div>

                    <div className="col-auto d-flex align-items-center">⇄</div>

                    <div className="col">
                        <small className="text-uppercase text-muted fw-semibold">
                            {labelDer}
                        </small>

                        {der.slice(0, 3).map((f, i) => (
                            <ChipFigurita key={i} figurita={f} />
                        ))}

                        {der.length > 3 && (
                            <small className="text-muted">
                                +{der.length - 3} más
                            </small>
                        )}
                    </div>
                </div>

                {(intercambio.tiempo || intercambio.fecha) && (
                    <small className="text-muted d-block mt-2">
                        {intercambio.tiempo || intercambio.fecha}
                    </small>
                )}

                <div className="d-flex gap-2 mt-3">
                    {botones}
                </div>

            </div>
        </div>
    );
};

export default IntercambioCard;