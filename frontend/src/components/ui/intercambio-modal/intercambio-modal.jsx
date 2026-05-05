const ChipFigurita = ({ figurita }) => (
    <div className="border rounded p-2 mb-1 d-flex align-items-center gap-2">
        <small className="text-muted">#{figurita.numero}</small>
        <span>{figurita.nombre}</span>
        {figurita.pais && <small className="text-muted">· {figurita.pais}</small>}
    </div>
);

const IntercambioModal = ({
    selected,
    onClose,
    izquierda,
    derecha,
    labelIzq,
    labelDer,
    extraBotones
}) => {
    if (!selected) return null;

    const izq = selected[izquierda] || [];
    const der = selected[derecha] || [];

    return (
        <div onClick={onClose} style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.4)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 999
        }}>
            <div onClick={e => e.stopPropagation()} style={{
                width: "750px",
                height: "600px",
                background: "white",
                borderRadius: "16px",
                display: "flex",
                flexDirection: "column",
                overflow: "hidden"
            }}>

                <div className="position-relative text-center p-3">
                    <h5 className="fw-bold m-0">Detalle del intercambio</h5>
                    <button onClick={onClose}
                        style={{ position: "absolute", right: 15, top: 10, border: "none", background: "transparent" }}>
                        ✖
                    </button>
                </div>

                <hr className="m-0" />

                <div className="p-3">
                    <strong>{selected.usuario.nombre}</strong>
                    <div className="text-muted">
                        {selected.tiempo || selected.fecha}
                    </div>
                </div>

                <hr className="m-0" />

                <div style={{ flex: 1, overflowY: "auto", padding: "16px" }}>
                    <div className="row">
                        <div className="col">
                            <small className="text-muted">{labelIzq}</small>
                            {izq.map((f, i) => <ChipFigurita key={i} figurita={f} />)}
                        </div>

                        <div className="col-auto d-flex align-items-center">⇄</div>

                        <div className="col">
                            <small className="text-muted">{labelDer}</small>
                            {der.map((f, i) => <ChipFigurita key={i} figurita={f} />)}
                        </div>
                    </div>
                </div>

                {extraBotones && (
                    <>
                        <hr className="m-0" />
                        <div className="p-3 d-flex gap-2">
                            {extraBotones}
                        </div>
                    </>
                )}

            </div>
        </div>
    );
};

export default IntercambioModal;