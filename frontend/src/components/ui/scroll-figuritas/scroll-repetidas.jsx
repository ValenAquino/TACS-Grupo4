import { useState } from "react";
 
const ScrollRepetidas = ({ figuritas = [], loading = false, seleccionada, onSeleccionar }) => {
  const [busqueda, setBusqueda] = useState("");
 
  const filtradas = figuritas.filter(
    (f) =>
      f.jugador.toLowerCase().includes(busqueda.toLowerCase()) ||
      f.numero?.toString().includes(busqueda),
  );
 
  return (
    <div className="d-flex flex-column gap-3">
      {/* Buscador */}
      <div className="position-relative">
        <span
          className="position-absolute top-50 translate-middle-y ms-3 text-muted"
          style={{ fontSize: "0.9rem" }}
        >
          🔍
        </span>
        <input
          type="text"
          className="form-control ps-5"
          placeholder="Buscar en tus repetidas..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          style={{ borderRadius: "0.5rem" }}
        />
      </div>
 
      {/* Lista */}
      <div className="border rounded-3 overflow-hidden">
        {/* Header */}
        <div
          className="d-flex justify-content-between align-items-center px-3 py-2 border-bottom"
          style={{ backgroundColor: "#fafafa" }}
        >
          <span
            className="fw-semibold text-uppercase text-muted"
            style={{ fontSize: "0.72rem", letterSpacing: "0.08em" }}
          >
            Tus repetidas
          </span>
          <span className="text-muted" style={{ fontSize: "0.78rem" }}>
            {seleccionada ? (
              <span className="text-success fw-semibold">{seleccionada.jugador}</span>
            ) : (
              "Ninguna seleccionada"
            )}
          </span>
        </div>
 
        <div style={{ maxHeight: "240px", overflowY: "auto" }}>
          {loading ? (
            <Skeleton />
          ) : filtradas.length === 0 ? (
            <SinResultados />
          ) : (
            filtradas.map((fig) => {
              const esSel = seleccionada?.figurita_id === fig.figurita_id;
              const reservadas = fig.cantidad_reservada;
              const disponibles = fig.cantidad_existente - reservadas - (esSel ? 1 : 0);
              const sinStock = disponibles === 0;
 
              return (
                <button
                  key={fig.figurita_id}
                  className={`w-100 d-flex align-items-center gap-3 px-3 py-2 border-0 border-bottom text-start ${
                    esSel ? "bg-success bg-opacity-10" : sinStock ? "bg-light" : "bg-white"
                  }`}
                  onClick={() => !sinStock && onSeleccionar(esSel ? null : fig)}
                  disabled={sinStock}
                  style={{ cursor: sinStock ? "not-allowed" : "pointer" }}
                >
                  {/* Radio visual */}
                  <div
                    className={`rounded-circle border d-flex align-items-center justify-content-center flex-shrink-0 ${
                      esSel ? "border-success" : sinStock ? "border-secondary opacity-25" : "border-secondary"
                    }`}
                    style={{ width: 18, height: 18 }}
                  >
                    {esSel && (
                      <div className="rounded-circle bg-success" style={{ width: 10, height: 10 }} />
                    )}
                  </div>
 
                  {/* Info */}
                  <div className={`flex-grow-1 ${sinStock ? "opacity-50" : ""}`}>
                    <p className="mb-0 fw-semibold" style={{ fontSize: "0.88rem" }}>
                      {fig.jugador}
                    </p>
                    <p className="mb-0 text-muted" style={{ fontSize: "0.75rem" }}>
                      {fig.seleccion} · #{fig.numero}
                    </p>
                  </div>
 
                  {/* Badge cantidad */}
                  <span
                    className="badge rounded-pill"
                    style={{
                      backgroundColor: sinStock ? "#f3f4f6" : "#e1f5ee",
                      color: sinStock ? "#9ca3af" : "#0f6e56",
                      fontSize: "0.72rem",
                    }}
                  >
                    {sinStock ? (
                      "Reservadas"
                    ) : (
                      <>
                        ×{disponibles}
                        {(reservadas > 0 || esSel) && (
                          <span style={{ opacity: 0.65 }}> de {fig.cantidad_existente}</span>
                        )}
                      </>
                    )}
                  </span>
                </button>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
};
 
const Skeleton = () => (
  <div className="d-flex flex-column gap-0">
    {[...Array(4)].map((_, i) => (
      <div key={i} className="placeholder-glow px-3 py-3 border-bottom">
        <div className="placeholder rounded w-75" style={{ height: "18px" }} />
      </div>
    ))}
  </div>
);
 
const SinResultados = () => (
  <div className="text-center text-muted py-4" style={{ fontSize: "0.85rem" }}>
    No hay resultados
  </div>
);
 
export default ScrollRepetidas;