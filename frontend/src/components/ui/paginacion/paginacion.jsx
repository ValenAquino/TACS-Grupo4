export const Paginacion = ({ page, totalPages, onChange }) => {
    return (
        <div className="d-flex align-items-center justify-content-center gap-3 flex-wrap">

            <button
                type="button"
                className="btn btn-outline-secondary"
                disabled={page <= 1}
                onClick={() => onChange(page - 1)}
            >
                Anterior
            </button>

            <span className="fw-semibold">
                Página {page} de {totalPages}
            </span>

            <button
                type="button"
                className="btn btn-outline-secondary"
                disabled={page >= totalPages}
                onClick={() => onChange(page + 1)}
            >
                Siguiente
            </button>

        </div>
    );
};

export default Paginacion;