import { useState } from "react";

const METODOS = ["INTERCAMBIO", "SUBASTA"];

const EditarRepetidaModal = ({ figurita, onClose, onGuardar }) => {
  const [cantidadExistente, setCantidadExistente] = useState(
    figurita.cantidad_existente
  );

  const [metodosSeleccionados, setMetodosSeleccionados] = useState(
    figurita.metodos
  );

  const toggleMetodo = (metodo) => {
    if (metodosSeleccionados.includes(metodo)) {
      setMetodosSeleccionados(
        metodosSeleccionados.filter((m) => m !== metodo)
      );
    } else {
      setMetodosSeleccionados([...metodosSeleccionados, metodo]);
    }
  };

  const handleGuardar = () => {
    onGuardar(figurita.figurita_id, {
      cantidad_existente: Number(cantidadExistente),
      metodos: metodosSeleccionados,
    });

    onClose();
  };

  return (
    <div
      className="modal d-block"
      style={{ backgroundColor: "rgba(0,0,0,0.4)" }}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content rounded-3">
          <div className="modal-header border-0 pb-0">
            <h6 className="modal-title fw-bold">
              Editar repetida ({figurita.jugador})
            </h6>
          </div>

          <div className="modal-body pt-2 d-flex flex-column gap-3">
            <div>
              <label
                className="form-label fw-semibold"
                style={{ fontSize: "0.9rem" }}
              >
                Cantidad existente
              </label>

              <input
                type="number"
                min={0}
                className="form-control"
                value={cantidadExistente}
                onChange={(e) => setCantidadExistente(e.target.value)}
                style={{ fontSize: "0.9rem" }}
              />
            </div>

            <div>
              <label
                className="form-label fw-semibold"
                style={{ fontSize: "0.9rem" }}
              >
                Métodos de intercambio
              </label>

              <div className="d-flex flex-column gap-2">
                {METODOS.map((metodo) => (
                  <div key={metodo} className="form-check">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      checked={metodosSeleccionados.includes(metodo)}
                      onChange={() => toggleMetodo(metodo)}
                    />

                    <label
                      className="form-check-label"
                      style={{ fontSize: "0.88rem" }}
                    >
                      {metodo}
                    </label>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="modal-footer border-0 pt-0 gap-2">
            <button
              className="btn btn-outline-secondary"
              style={{ fontSize: "0.85rem" }}
              onClick={onClose}
            >
              Cancelar
            </button>

            <button
              className="btn btn-success"
              style={{ fontSize: "0.85rem" }}
              onClick={handleGuardar}
            >
              Guardar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditarRepetidaModal;