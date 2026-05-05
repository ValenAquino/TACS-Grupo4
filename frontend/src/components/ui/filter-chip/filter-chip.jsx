const FilterChip = ({ label, selected = false, onClick }) => {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`btn rounded-pill px-3 py-1 ${
                selected
                    ? 'btn-success border-0 fw-semibold'
                    : 'btn-outline-secondary fw-normal'
            }`}
            style={{ fontSize: '0.85rem', fontFamily: 'var(--font-family)' }}
        >
            {label}
        </button>
    );
};

export default FilterChip;