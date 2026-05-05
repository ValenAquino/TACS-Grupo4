// ─── ScrollRepetidas ──────────────────────────────────────────────────────────
//
// Lista con scroll de figuritas repetidas.
// Soporta dos modos de selección:
//
//  modo="unica" (default) → radio button, una sola figurita
//    - seleccionada: figurita | null
//    - onSeleccionar: (fig | null) => void
//
//  modo="multiple" → checkbox, varias figuritas
//    - seleccionadas: figurita[]
//    - onToggle: (fig) => void
//    - bloqueadas: figurita[]  → marcadas y no desmarcables (obligatorias)
//
// Props comunes:
//  - figuritas: array de repetidas (cargadas externamente)
//  - loading: boolean

import { useState } from "react";

const ScrollRepetidas = ({
  figuritas = [],
  loading = false,
  modo = "unica",
  // modo unica
  seleccionada = null,
  onSeleccionar,
  // modo multiple
  seleccionadas = [],
  onToggle,
  bloqueadas = [], // figuritas obligatorias: siempre marcadas, no se pueden desmarcar
}) => {
  const [busqueda, setBusqueda] = useState("");

  const filtradas = figuritas.filter(
    (f) =>
      f.jugador.toLowerCase().includes(busqueda.toLowerCase()) ||
      f.numero?.toString().includes(busqueda),
  );

  // ── Helpers ────────────────────────────────────────────────────────────────
  const esBloqueada = (fig) =>
    bloqueadas.some((b) => b.figurita_id === fig.figurita_id);

  const estaSeleccionadaUnica = (fig) =>
    seleccionada?.figurita_id === fig.figurita_id;

  const estaSeleccionadaMultiple = (fig) =>
    esBloqueada(fig) || seleccionadas.some((f) => f.figurita_id === fig.figurita_id);

  const calcularDisponibles = (fig, esSel) =>
    fig.cantidad_existente - fig.cantidad_reservada - (esSel ? 1 : 0);

  const totalSeleccionadas = seleccionadas.length + bloqueadas.length;

  // ── Header derecho ─────────────────────────────────────────────────────────
  const renderHeaderDerecho = () => {
    if (modo === "unica") {
      return seleccionada ? (
        <span className="text-success fw-semibold">{seleccionada.jugador}</span>
      ) : (
        "Ninguna seleccionada"
      );
    }
    return totalSeleccionadas > 0 ? (
      <span className="text-success fw-semibold">
        {totalSeleccionadas} seleccionada{totalSeleccionadas > 1 ? "s" : ""}
      </span>
    ) : (
      "Ninguna seleccionada"
    );
  };

  // ── Fila ───────────────────────────────────────────────────────────────────
  const renderFila = (fig) => {
    const reservadas = fig.cantidad_reservada;

    if (modo === "unica") {
      const esSel = estaSeleccionadaUnica(fig);
      const disponibles = calcularDisponibles(fig, esSel);
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
          <RadioVisual esSel={esSel} sinStock={sinStock} />
          <FilaInfo fig={fig} opaco={sinStock} />
          <BadgeCantidad sinStock={sinStock} disponibles={disponibles} reservadas={reservadas} esSel={esSel} total={fig.cantidad_existente} />
        </button>
      );
    }

    // modo multiple
    const bloqueada = esBloqueada(fig);
    const esSel = estaSeleccionadaMultiple(fig); // true si bloqueada o seleccionada
    const disponibles = calcularDisponibles(fig, false);
    const sinStock = disponibles === 0;

    // Una figurita bloqueada sin stock es un problema: el usuario no la tiene disponible
    // pero la subasta la requiere. Se muestra con estilo de advertencia.
    const bloqueadaSinStock = bloqueada && sinStock;

    return (
      <button
        key={fig.figurita_id}
        className={`w-100 d-flex align-items-center gap-3 px-3 py-2 border-0 border-bottom text-start ${
          bloqueadaSinStock
            ? "bg-warning bg-opacity-10"
            : esSel
              ? "bg-success bg-opacity-10"
              : sinStock
                ? "bg-light"
                : "bg-white"
        }`}
        onClick={() => !bloqueada && !sinStock && onToggle(fig)}
        disabled={bloqueada || sinStock}
        style={{ cursor: bloqueada || sinStock ? "default" : "pointer" }}
      >
        {/* Checkbox visual */}
        <div
          className={`rounded d-flex align-items-center justify-content-center flex-shrink-0 border ${
            bloqueadaSinStock
              ? "bg-warning border-warning"
              : esSel
                ? "bg-success border-success"
                : sinStock
                  ? "border-secondary opacity-25"
                  : "border-secondary"
          }`}
          style={{ width: 18, height: 18 }}
        >
          {esSel && !bloqueadaSinStock && (
            <span style={{ color: "#fff", fontSize: "0.65rem" }}>✓</span>
          )}
          {bloqueadaSinStock && (
            <span style={{ color: "#fff", fontSize: "0.65rem" }}>!</span>
          )}
        </div>

        <FilaInfo fig={fig} opaco={sinStock && !bloqueada} />

        <div className="d-flex align-items-center gap-1">
          {bloqueada && (
            <span
              className="badge rounded-pill"
              style={{
                backgroundColor: bloqueadaSinStock ? "#fef3c7" : "#e1f5ee",
                color: bloqueadaSinStock ? "#92400e" : "#0f6e56",
                fontSize: "0.68rem",
              }}
            >
              {bloqueadaSinStock ? "Sin stock" : "Requerida"}
            </span>
          )}
          <BadgeCantidad
            sinStock={sinStock}
            disponibles={disponibles}
            reservadas={reservadas}
            esSel={esSel}
            total={fig.cantidad_existente}
          />
        </div>
      </button>
    );
  };

  // ── Render ─────────────────────────────────────────────────────────────────
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
            {renderHeaderDerecho()}
          </span>
        </div>

        <div style={{ maxHeight: "240px", overflowY: "auto" }}>
          {loading ? (
            <Skeleton />
          ) : filtradas.length === 0 ? (
            <SinResultados />
          ) : (
            filtradas.map(renderFila)
          )}
        </div>
      </div>
    </div>
  );
};

// ── Subcomponentes ─────────────────────────────────────────────────────────────

const RadioVisual = ({ esSel, sinStock }) => (
  <div
    className={`rounded-circle border d-flex align-items-center justify-content-center flex-shrink-0 ${
      esSel ? "border-success" : sinStock ? "border-secondary opacity-25" : "border-secondary"
    }`}
    style={{ width: 18, height: 18 }}
  >
    {esSel && <div className="rounded-circle bg-success" style={{ width: 10, height: 10 }} />}
  </div>
);

const FilaInfo = ({ fig, opaco }) => (
  <div className={`flex-grow-1 ${opaco ? "opacity-50" : ""}`}>
    <p className="mb-0 fw-semibold" style={{ fontSize: "0.88rem" }}>
      {fig.jugador}
    </p>
    <p className="mb-0 text-muted" style={{ fontSize: "0.75rem" }}>
      {fig.seleccion} · #{fig.numero}
    </p>
  </div>
);

const BadgeCantidad = ({ sinStock, disponibles, reservadas, esSel, total }) => (
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
          <span style={{ opacity: 0.65 }}> de {total}</span>
        )}
      </>
    )}
  </span>
);

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