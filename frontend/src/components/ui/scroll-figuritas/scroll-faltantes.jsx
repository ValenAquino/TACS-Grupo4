import { useState } from "react";
 
const ScrollFaltantes = ({ figuritas = [], loading = false, seleccionadas = [], onToggle }) => {
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
          placeholder="Buscar en tus faltantes..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          style={{ borderRadius: "0.5rem" }}
        />
      </div>
 
      {/* Lista */}
      <div className="border rounded-3" style={{ maxHeight: "240px", overflowY: "auto" }}>
        {loading ? (
          <Skeleton />
        ) : filtradas.length === 0 ? (
          <SinResultados />
        ) : (
          filtradas.map((fig) => {
            const marcada = seleccionadas.some((f) => f.id === fig.id);
 
            return (
              <button
                key={fig.id}
                className={`w-100 d-flex align-items-center gap-3 px-3 py-2 border-0 border-bottom text-start ${
                  marcada ? "bg-success bg-opacity-10" : "bg-white"
                }`}
                onClick={() => onToggle(fig)}
              >
                {/* Checkbox visual */}
                <div
                  className={`rounded d-flex align-items-center justify-content-center flex-shrink-0 border ${
                    marcada ? "bg-success border-success" : "border-secondary"
                  }`}
                  style={{ width: 18, height: 18 }}
                >
                  {marcada && (
                    <span style={{ color: "#fff", fontSize: "0.65rem" }}>✓</span>
                  )}
                </div>
 
                {/* Info */}
                <div className="flex-grow-1">
                  <p className="mb-0 fw-semibold" style={{ fontSize: "0.85rem" }}>
                    {fig.jugador}
                  </p>
                  <p className="mb-0 text-muted" style={{ fontSize: "0.73rem" }}>
                    {fig.seleccion} · #{fig.numero}
                  </p>
                </div>
              </button>
            );
          })
        )}
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
 
export default ScrollFaltantes;