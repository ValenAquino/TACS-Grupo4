import { useState } from "react";

const CalificarModal = ({ show, usuario, onConfirmar, onCancelar }) => {
  const [valor, setValor] = useState(0);
  const [hover, setHover] = useState(0);
  const [descripcion, setDescripcion] = useState("");
  const [loading, setLoading] = useState(false);

  if (!show) return null;

  const handleConfirmar = async () => {
    if (valor === 0) return;
    try {
      setLoading(true);
      await onConfirmar({ valor, descripcion: descripcion.trim() || null });
    } finally {
      setLoading(false);
      setValor(0);
      setDescripcion("");
    }
  };

  const handleCancelar = () => {
    setValor(0);
    setHover(0);
    setDescripcion("");
    onCancelar();
  };

  return (
    <div
      className="modal d-block"
      style={{ backgroundColor: "rgba(0,0,0,0.4)" }}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content rounded-3">
          <div className="modal-header border-0 pb-0">
            <h6 className="modal-title fw-bold">Calificar a {usuario}</h6>
          </div>
          <div className="modal-body pt-2 d-flex flex-column gap-3">
            {/* Estrellas */}
            <div className="d-flex gap-1 justify-content-center">
              {[1, 2, 3, 4, 5].map((estrella) => (
                <span
                  key={estrella}
                  style={{
                    fontSize: "2rem",
                    cursor: "pointer",
                    color: estrella <= (hover || valor) ? "#f5a623" : "#dee2e6",
                    transition: "color 0.1s",
                  }}
                  onClick={() => setValor(estrella)}
                  onMouseEnter={() => setHover(estrella)}
                  onMouseLeave={() => setHover(0)}
                >
                  ★
                </span>
              ))}
            </div>

            {/* Comentario opcional */}
            <div>
              <textarea
                className="form-control"
                placeholder="Comentario opcional..."
                rows={3}
                style={{ fontSize: "0.88rem", resize: "none" }}
                value={descripcion}
                maxLength={255}
                onChange={(e) => setDescripcion(e.target.value)}
              />
              <small
                className="text-muted d-block text-end mt-1"
                style={{ fontSize: "0.75rem" }}
              >
                {descripcion.length}/255
              </small>
            </div>
          </div>
          <div className="modal-footer border-0 pt-0 gap-2">
            <button
              className="btn btn-outline-secondary"
              style={{ fontSize: "0.85rem" }}
              onClick={handleCancelar}
            >
              Cancelar
            </button>
            <button
              className="btn btn-success"
              style={{ fontSize: "0.85rem" }}
              disabled={valor === 0 || loading}
              onClick={handleConfirmar}
            >
              {loading ? "Enviando..." : "Confirmar"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CalificarModal;