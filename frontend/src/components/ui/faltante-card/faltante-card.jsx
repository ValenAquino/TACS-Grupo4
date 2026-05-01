const FaltanteCard = ({ figurita }) => {
    const { id, numero, jugador, seleccion } = figurita;

    return (
        <div className="d-flex justify-content-between align-items-center px-3 py-2 border-bottom rounded-2"
            style={{ backgroundColor: "var(--color-primary)" }}
        >
            <div className="d-flex align-items-center gap-3">
                <span className="text-muted" style={{ fontSize: '0.8rem', minWidth: '32px' }}>
                  #{id}
                </span>
                <div>
                    <p className="mb-0 fw-semibold" style={{ fontSize: '0.9rem' }}>
                        {jugador}
                    </p>
                    <p className="mb-0 text-muted" style={{ fontSize: '0.75rem' }}>
                        {seleccion}
                    </p>
                </div>
            </div>
        </div>
    );
};

export default FaltanteCard;