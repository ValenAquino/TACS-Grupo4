import Button from "../button/button.jsx";

export const Paginacion = ({ page, totalPages, onChange }) => {
    return (
        <div className="d-flex align-items-center justify-content-center gap-3 flex-wrap">

            <Button
                type="button"
                className="btn btn-outline-secondary"
                disabled={page <= 1}
                onClick={() => onChange(page - 1)}
            >
                Anterior
            </Button>

            <span className="fw-semibold">
                Página {page} de {totalPages}
            </span>

            <Button
                type="button"
                className="btn btn-outline-secondary"
                disabled={page >= totalPages}
                onClick={() => onChange(page + 1)}
            >
                Siguiente
            </Button>

        </div>
    );
};

export default Paginacion;