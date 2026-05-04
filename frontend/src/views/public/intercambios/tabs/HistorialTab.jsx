// ─── Componentes internos ─────────────────────────

const BadgeEstado = ({ etiqueta, color = "secondary" }) => (
    <span
        className={`badge text-bg-${color} ms-2`}
        style={{
            fontSize: "0.85rem",
            padding: "6px 10px",
            borderRadius: "8px"
        }}
    >
        {etiqueta}
    </span>
);

const ChipFigurita = ({ figurita }) => (
    <div className="border rounded p-2 mb-1 d-flex align-items-center gap-2">
        <small className="text-muted">#{figurita.numero}</small>
        <span>{figurita.nombre}</span>
        <small className="text-muted">· {figurita.pais}</small>
    </div>
);

// ─── Tab ─────────────────────────────────────────────────────────────────────

const HistorialTab = ({ intercambios }) => (
    <div>
        <h6 className="fw-bold mt-3">CONCRETADOS ({intercambios.length})</h6>
        {intercambios.map((intercambio) => (
            <div key={intercambio.id} className="card mb-3">
                <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start">
                        <strong>{intercambio.usuario.nombre}</strong>
                        <BadgeEstado etiqueta="Concretado" color="success" />
                    </div>

                    <div className="row mt-2">
                        <div className="col">
                            <small className="text-uppercase text-muted fw-semibold">Recibiste</small>
                            {intercambio.recibiste.map((f, i) => (
                                <ChipFigurita key={i} figurita={f} />
                            ))}
                        </div>
                        <div className="col-auto d-flex align-items-center">⇄</div>
                        <div className="col">
                            <small className="text-uppercase text-muted fw-semibold">Entregaste</small>
                            {intercambio.entregaste.map((f, i) => (
                                <ChipFigurita key={i} figurita={f} />
                            ))}
                        </div>
                    </div>

                    <small className="text-muted d-block mt-2">
                        {intercambio.fecha} ·{" "}
                        {intercambio.calificado
                            ? "Ya calificaste a este usuario"
                            : "Pendiente de calificación"}
                    </small>

                    <div className="d-flex gap-2 mt-3">
                        {!intercambio.calificado && (
                            <button className="btn btn-outline-warning btn-sm flex-fill">
                                ★ Calificar usuario
                            </button>
                        )}
                        <button className="btn btn-outline-dark btn-sm flex-fill">Ver detalle</button>
                    </div>
                </div>
            </div>
        ))}
    </div>
);

export default HistorialTab;