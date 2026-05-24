const BadgeEstado = ({ estado }) => {

    const colores = {
        PENDIENTE: "warning",
        ACEPTADO: "success",
        RECHAZADO: "danger",
        ENVIADA: "primary"
    };

    return (
        <span
            className={`badge text-bg-${colores[estado] || "secondary"}`}
            style={{
                fontSize: "0.8rem",
                padding: "6px 10px",
                borderRadius: "10px"
            }}
        >
            {estado}
        </span>
    );
};

const renderStars = (rating) => {

    const fullStars = Math.floor(rating);
    const emptyStars = 5 - fullStars;

    return (
        <>
            {"★".repeat(fullStars)}
            {"☆".repeat(emptyStars)}
        </>
    );
};

const HeaderUsuarioEstado = ({ destinatario, estado }) => {
    return (
        <div className="d-flex justify-content-between align-items-center">

            <div className="d-flex align-items-center gap-3">

                <div
                    className="rounded-circle d-flex align-items-center justify-content-center fw-bold"
                    style={{
                        width: "42px",
                        height: "42px",
                        backgroundColor: "#d9f5ef",
                        color: "#4b5563"
                    }}
                >
                    {destinatario.iniciales}
                </div>

                <div className="d-flex flex-column">

                <span className="fw-semibold">
                    {destinatario.nombre}
                </span>

                    <small className="text-muted">
                        {renderStars(destinatario.calificacion_media)}
                        {" " + destinatario.calificacion_media}
                    </small>

                </div>
            </div>

            {estado && (
                <BadgeEstado estado={estado} />
            )}

        </div>
    )
}

export default HeaderUsuarioEstado;