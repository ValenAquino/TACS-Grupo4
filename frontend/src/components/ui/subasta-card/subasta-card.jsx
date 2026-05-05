import { derivarTiempo } from "../../../utils/subastasTiempo.js";
import useUsuarioActual from "../../../hooks/useUsuarioActual.js";
import { calificarPerfil } from "../../../services/perfilService.js";
import CalificarModal from "../calificar-modal/calificar-modal.jsx";
import { useState } from "react";
import {useNavigate} from "react-router";
import {mostrar_label} from "../../../utils/estandarizar.jsx";

const BADGE_OFERTA = {
  SELECCIONADO: {
    label: "Mejor oferta",
    className: "text-success bg-success-subtle",
  },
  RECHAZADO: { label: "No elegida", className: "text-danger bg-danger-subtle" },
  PENDIENTE: { label: "No elegida", className: "text-danger bg-danger-subtle" },
  ACEPTADO: { label: "Elegida", className: "text-success bg-success-subtle" },
};

const SubastaCard = ({subasta}) => {
  const { userId } = useUsuarioActual();
  const { id, autor, figurita_subastada, fecha_cierre, tu_oferta } = subasta;
  const [mostrarCalificar, setMostrarCalificar] = useState(false);
  const navigate = useNavigate();

  const { finalizada, tiempoRestante, finalizadaHace, finalizaPronto } =
    derivarTiempo({ fecha_cierre });

  const badgeEstado = finalizada
    ? null
    : finalizaPronto
      ? {
          label: "⏱ Finaliza pronto",
          className: "text-warning bg-warning-subtle",
        }
      : { label: "Activa", className: "text-success bg-success-subtle" };

  const badgeOferta = tu_oferta.estado ? (BADGE_OFERTA[tu_oferta.estado] ?? null) : null;

  const handleCalificar = async ({ valor, descripcion }) => {
    await calificarPerfil(userId, autor.id, { valor, descripcion, transactionId: id, tipoTransaccion: "SUBASTA" });
    setMostrarCalificar(false);
  };

  const puedoCalificar = finalizada && tu_oferta.estado === "ACEPTADO" && !subasta.ya_calificado;

  return (
    <div className="border rounded-3 overflow-hidden bg-white">
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center px-3 py-2 border-bottom">
        <div className="d-flex align-items-center gap-2">
          <div
            className="d-flex align-items-center justify-content-center rounded-circle border"
            style={{
              width: 40,
              height: 40,
              fontSize: "1.1rem",
              backgroundColor: "#f0f9f4",
            }}
          >
            ⚽
          </div>
          <div>
            <p className="mb-0 fw-semibold" style={{ fontSize: "0.95rem" }}>
              {figurita_subastada.jugador}
            </p>
            <p className="mb-0 text-muted" style={{ fontSize: "0.75rem" }}>
              {figurita_subastada.seleccion?.nombre} · #
              {figurita_subastada.numero}
            </p>
          </div>
        </div>

        {badgeEstado && (
          <span
            className={`badge rounded-pill px-2 py-1 ${badgeEstado.className}`}
            style={{ fontSize: "0.72rem", fontWeight: 500 }}
          >
            {badgeEstado.label}
          </span>
        )}
      </div>
      {/* Tiempo + autor */}
      <div
        className="px-3 py-2 d-flex justify-content-between align-items-center"
        style={{ backgroundColor: "#fafafa", fontSize: "0.82rem" }}
      >
        <span className="text-muted">
          {finalizada ? (
            <>
              ● Finalizada hace <strong>{finalizadaHace}</strong>
            </>
          ) : (
            <>
              ● Tiempo restante <strong>{tiempoRestante}</strong>
            </>
          )}
        </span>
        <span className="text-muted">
          Publicada por <strong>{autor.nombre}</strong>
        </span>
      </div>
      {/* Tu oferta — solo visible en tab Participo */}
      {tu_oferta.estado && (
        <div className="px-3 py-2 d-flex justify-content-between align-items-center border-top">
          <span className="text-muted" style={{ fontSize: "0.82rem" }}>
            Tu oferta
          </span>
          <div className="d-flex align-items-center gap-2">
            <span style={{ fontSize: "0.85rem" }}>{mostrar_label(tu_oferta)}</span>
            {badgeOferta && (
              <span
                className={`badge rounded-pill px-2 py-1 ${badgeOferta.className}`}
                style={{ fontSize: "0.7rem", fontWeight: 500 }}
              >
                {badgeOferta.label}
              </span>
            )}
          </div>
        </div>
      )}
      {/* Acciones */}
      <div className="px-3 py-2 d-flex gap-2 border-top">
          <button
              className="btn btn-outline-secondary flex-fill"
              style={{ fontSize: "0.85rem" }}
              onClick={() => navigate(`/subastas/${subasta.id}`)}
          >
              {finalizada ? "Ver resumen" : "Ver subasta"}
          </button>
        {finalizada ? (
          <>
            {puedoCalificar && (
              <button
                className="btn btn-outline-secondary flex-fill"
                style={{ fontSize: "0.85rem" }}
                onClick={() => setMostrarCalificar(true)}
              >
                Calificar usuario
              </button>
            )}
          </>
        ) : (
          <>
            {mostrar_label(tu_oferta) && (
              <button
                className="btn btn-outline-secondary flex-fill"
                style={{ fontSize: "0.85rem" }}
                onClick={() => navigate(`/subastas/${subasta.id}/ofertas/${tu_oferta.id}`)}
              >
                Mejorar oferta
              </button>
            )}
          </>
        )}
      </div>
      <CalificarModal
        show={mostrarCalificar}
        usuario={autor.nombre}
        onConfirmar={handleCalificar}
        onCancelar={() => setMostrarCalificar(false)}
      />
    </div>
  );
};

export default SubastaCard;
