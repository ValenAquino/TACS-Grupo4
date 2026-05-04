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

const EnviadasTab = ({ esperando, resueltas }) => (
    <div>
        <h6 className="fw-bold mt-3">ESPERANDO RESPUESTA ({esperando.length})</h6>
        {esperando.map((intercambio) => (
            <div key={intercambio.id} className="card mb-3">
                <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start">
                        <strong>{intercambio.usuario.nombre}</strong>
                        <BadgeEstado etiqueta="Enviada" color="primary" />
                    </div>

                    <div className="row mt-2">
                        <div className="col">
                            <small className="text-uppercase text-muted fw-semibold">Vos pedís</small>
                            {intercambio.pedis.map((f, i) => (
                                <ChipFigurita key={i} figurita={f} />
                            ))}
                        </div>
                        <div className="col-auto d-flex align-items-center">⇄</div>
                        <div className="col">
                            <small className="text-uppercase text-muted fw-semibold">Vos ofrecés</small>
                            {intercambio.ofreces.map((f, i) => (
                                <ChipFigurita key={i} figurita={f} />
                            ))}
                        </div>
                    </div>

                    <small className="text-muted d-block mt-2">{intercambio.tiempo}</small>

                    <div className="d-flex gap-2 mt-3">
                        <button className="btn btn-outline-dark btn-sm flex-fill">Ver detalle</button>
                        <button className="btn btn-outline-dark btn-sm flex-fill">Cancelar</button>
                    </div>
                </div>
            </div>
        ))}

        {resueltas.length > 0 && (
            <>
                <h6 className="fw-bold mt-4">RESUELTAS ({resueltas.length})</h6>
                {resueltas.map((intercambio) => (
                    <div key={intercambio.id} className="card mb-3">
                        <div className="card-body">
                            <div className="d-flex justify-content-between align-items-start">
                                <strong>{intercambio.usuario.nombre}</strong>
                                <BadgeEstado etiqueta={intercambio.estado} color={intercambio.colorEstado} />
                            </div>

                            <div className="row mt-2">
                                <div className="col">
                                    <small className="text-uppercase text-muted fw-semibold">Pediste</small>
                                    {intercambio.pediste.map((f, i) => (
                                        <ChipFigurita key={i} figurita={f} />
                                    ))}
                                </div>
                                <div className="col-auto d-flex align-items-center">⇄</div>
                                <div className="col">
                                    <small className="text-uppercase text-muted fw-semibold">Ofreciste</small>
                                    {intercambio.ofreciste.map((f, i) => (
                                        <ChipFigurita key={i} figurita={f} />
                                    ))}
                                </div>
                            </div>

                            <small className="text-muted d-block mt-2">{intercambio.tiempo}</small>

                            <div className="d-flex gap-2 mt-3">
                                {intercambio.puedeCalificar && (
                                    <button className="btn btn-outline-warning btn-sm flex-fill">
                                        ★ Calificar usuario
                                    </button>
                                )}
                                <button className="btn btn-outline-dark btn-sm flex-fill">Ver detalle</button>
                            </div>
                        </div>
                    </div>
                ))}
            </>
        )}
    </div>
);

export default EnviadasTab;