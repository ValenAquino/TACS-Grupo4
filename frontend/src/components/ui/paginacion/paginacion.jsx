export const Paginacion = ({ page, totalPages, onChange }) => {
    if (totalPages <= 1) return null

    return (
        <div className="d-flex align-items-center justify-content-center gap-3 pt-2">
            <button
                className="btn btn-outline-secondary btn-sm"
                onClick={() => onChange(page - 1)}
                disabled={page <= 1}
            >
                ← Anterior
            </button>

            <span className="text-muted small">
                {page} / {totalPages}
            </span>

            <button
                className="btn btn-outline-secondary btn-sm"
                onClick={() => onChange(page + 1)}
                disabled={page >= totalPages}
            >
                Siguiente →
            </button>
        </div>
    )
}

export default Paginacion
