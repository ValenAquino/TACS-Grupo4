const Pagination = ({ page, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null

  return (
    <div className="d-flex align-items-center justify-content-center gap-3 pt-2">
      <button
        className="btn btn-outline-secondary btn-sm"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
      >
        ← Anterior
      </button>
      <span className="text-muted small">
        {page + 1} / {totalPages}
      </span>
      <button
        className="btn btn-outline-secondary btn-sm"
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
      >
        Siguiente →
      </button>
    </div>
  )
}

export default Pagination
