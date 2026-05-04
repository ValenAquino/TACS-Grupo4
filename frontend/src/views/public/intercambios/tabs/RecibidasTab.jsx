import { useState } from "react";

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

// ─── Tab ─────────────────────────────────────────

const RecibidasTab = ({ pendientes, resueltas }) => {

    const [selected, setSelected] = useState(null);

    return (
        <div>

            {/* ESTILOS */}
            <style>{`
                .btn-aceptar {
                  border: 1px solid #175A2D;
                  color: #175A2D;
                  background-color: transparent;
                }

                .btn-aceptar:hover {
                  background-color: #175A2D;
                  color: white;
                }

                .btn-rechazar {
                  border: 1px solid #dc3545;
                  color: #dc3545;
                  background-color: transparent;
                }

                .btn-rechazar:hover {
                  background-color: #dc3545;
                  color: white;
                }
            `}</style>

            {/* PENDIENTES */}
            <h6 className="fw-bold mt-3">PENDIENTES ({pendientes.length})</h6>

            {pendientes.map((intercambio) => (
                <div key={intercambio.id} className="card mb-3 border-success border-2">
                    <div className="card-body">

                        <div className="d-flex justify-content-between align-items-start">
                            <strong>{intercambio.usuario.nombre}</strong>
                            {intercambio.esNueva && (
                                <BadgeEstado etiqueta="Nueva" color="warning" />
                            )}
                        </div>

                        <div className="row mt-2">
                            <div className="col">
                                <small className="text-uppercase text-muted fw-semibold">Te pide</small>
                                {intercambio.pide.map((f, i) => (
                                    <ChipFigurita key={i} figurita={f} />
                                ))}
                            </div>

                            <div className="col-auto d-flex align-items-center">⇄</div>

                            <div className="col">
                                <small className="text-uppercase text-muted fw-semibold">Te ofrece</small>
                                {intercambio.ofrece.map((f, i) => (
                                    <ChipFigurita key={i} figurita={f} />
                                ))}
                            </div>
                        </div>

                        <small className="text-muted d-block mt-2">
                            {intercambio.tiempo}
                        </small>

                        <div className="d-flex gap-2 mt-3">
                            <button className="btn btn-sm flex-fill btn-aceptar">
                                ✓ Aceptar
                            </button>

                            <button className="btn btn-sm flex-fill btn-rechazar">
                                ✗ Rechazar
                            </button>

                            <button
                                className="btn btn-outline-dark btn-sm flex-fill"
                                onClick={() => setSelected(intercambio)}
                            >
                                Ver más
                            </button>
                        </div>

                    </div>
                </div>
            ))}

            {/* RESUELTAS */}
            {resueltas.length > 0 && (
                <>
                    <h6 className="fw-bold mt-4">
                        RESUELTAS HOY ({resueltas.length})
                    </h6>

                    {resueltas.map((intercambio) => (
                        <div key={intercambio.id} className="card mb-3">
                            <div className="card-body">

                                <div className="d-flex justify-content-between align-items-start">
                                    <strong>{intercambio.usuario.nombre}</strong>
                                    <BadgeEstado
                                        etiqueta={intercambio.estado}
                                        color={intercambio.colorEstado}
                                    />
                                </div>

                                <div className="row mt-2">
                                    <div className="col">
                                        <small className="text-uppercase text-muted fw-semibold">
                                            Pedía
                                        </small>
                                        {intercambio.pedida.map((f, i) => (
                                            <ChipFigurita key={i} figurita={f} />
                                        ))}
                                    </div>

                                    <div className="col-auto d-flex align-items-center">⇄</div>

                                    <div className="col">
                                        <small className="text-uppercase text-muted fw-semibold">
                                            Ofrecía
                                        </small>
                                        {intercambio.ofrecida.map((f, i) => (
                                            <ChipFigurita key={i} figurita={f} />
                                        ))}
                                    </div>
                                </div>

                                <small className="text-muted d-block mt-2">
                                    {intercambio.tiempo} · {intercambio.nota}
                                </small>

                                <button
                                    className="btn btn-outline-dark btn-sm w-100 mt-3"
                                    onClick={() => setSelected(intercambio)}
                                >
                                    Ver detalle
                                </button>

                            </div>
                        </div>
                    ))}
                </>
            )}

            {/* MODAL */}
            {selected && (
                <div
                    onClick={() => setSelected(null)}
                    style={{
                        position: "fixed",
                        top: 0,
                        left: 0,
                        width: "100vw",
                        height: "100vh",
                        backgroundColor: "rgba(0,0,0,0.4)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        zIndex: 999
                    }}
                >
                    <div
                        onClick={(e) => e.stopPropagation()}
                        style={{
                            backgroundColor: "white",
                            borderRadius: "16px",
                            padding: "24px",
                            width: "700px",
                            maxWidth: "90vw",
                            maxHeight: "85vh",
                            overflowY: "auto"
                        }}
                    >
                        <div className="position-relative mb-3 text-center">
                            <h5 className="fw-bold mb-0">Detalle del intercambio</h5>
                            <button
                                onClick={() => setSelected(null)}
                                style={{
                                    position: "absolute",
                                    right: 0,
                                    top: 0,
                                    border: "none",
                                    background: "transparent",
                                    fontSize: "18px"
                                }}
                            >
                                ✖
                            </button>
                        </div>

                        <strong>{selected.usuario.nombre}</strong>
                        <div className="text-muted mb-3">{selected.tiempo}</div>

                        <div className="row">
                            <div className="col">
                                <small className="text-muted">Te pide</small>
                                {(selected.pide || selected.pedida || []).map((f, i) => (
                                    <ChipFigurita key={i} figurita={f} />
                                ))}
                            </div>

                            <div className="col-auto d-flex align-items-center">⇄</div>

                            <div className="col">
                                <small className="text-muted">Te ofrece</small>
                                {(selected.ofrece || selected.ofrecida || []).map((f, i) => (
                                    <ChipFigurita key={i} figurita={f} />
                                ))}
                            </div>
                        </div>

                        <div className="d-flex gap-2 mt-4">
                            <button className="btn btn-sm flex-fill btn-aceptar">
                                ✓ Aceptar
                            </button>
                            <button className="btn btn-sm flex-fill btn-rechazar">
                                ✗ Rechazar
                            </button>
                        </div>

                    </div>
                </div>
            )}

        </div>
    );
};

export default RecibidasTab;