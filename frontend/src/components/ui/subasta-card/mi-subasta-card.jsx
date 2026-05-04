import { useState } from "react";
import ConfirmModal from "../confirm-modal/confirm-modal.jsx";
import CalificarModal from "../calificar-modal/calificar-modal.jsx";
import {
  seleccionarOferta,
  rechazarOferta,
  cancelarSubasta,
  cerrarSubasta,
} from "../../../services/subastasService.js";
import { calificarPerfil } from "../../../services/perfilService.js";
import { derivarTiempo } from "../../../utils/subastasTiempo.js";
import useUsuarioActual from "../../../hooks/useUsuarioActual.js";


const BADGE_ESTADO = {
  activa: { label: "Activa", className: "text-success bg-success-subtle" },
  finaliza_pronto: {
    label: "Finaliza pronto",
    className: "text-warning bg-warning-subtle",
  },
  adjudicada: {
    label: "Adjudicada",
    className: "text-secondary bg-secondary-subtle",
  },
};

const MiSubastaCard = ({ subasta, onVerDetalle, onVerResumen, onRefresh }) => {
  const {
    id: subastaId,
    figuritaSubastada,
    fechaCierre,
    ofertas,
    cantidadOfertas,
    ganador,
    ya_calificado: yaCalificado,
  } = subasta;

  const { finalizada, tiempoRestante, finalizadaHace, finalizaPronto } =
    derivarTiempo({ fechaCierre });

  const { userId } = useUsuarioActual();

  const [modal, setModal] = useState(null);
  const [loadingModal, setLoadingModal] = useState(false);
  const [mostrarCalificar, setMostrarCalificar] = useState(false);

  const estadoKey = finalizada
    ? "adjudicada"
    : finalizaPronto
      ? "finaliza_pronto"
      : "activa";

  const badge = BADGE_ESTADO[estadoKey];
  const haySeleccionada = ofertas?.some((o) => o.seleccionada);

  const handleConfirmar = async () => {
    try {
      setLoadingModal(true);
      if (modal.tipo === "adjudicar") {
        await seleccionarOferta(subastaId, modal.ofertaId);
      } else if (modal.tipo === "rechazar") {
        await rechazarOferta(subastaId, modal.ofertaId);
      } else if (modal.tipo === "cancelar") {
        await cancelarSubasta(subastaId);
      } else if (modal.tipo === "cerrar") {
        await cerrarSubasta(subastaId);
      }
      setModal(null);
      onRefresh();
    } catch {
      // manejar error si es necesario
    } finally {
      setLoadingModal(false);
    }
  };

  const handleCalificar = async ({ valor, descripcion }) => {
    await calificarPerfil(userId, ganador.perfilId, { valor, descripcion, transactionId: subastaId,
    tipoTransaccion: "SUBASTA"});
    setMostrarCalificar(false);
    onRefresh();
  };

  const MODAL_CONFIG = {
    adjudicar: {
      titulo: "Adjudicar oferta",
      mensaje:
        "¿Querés seleccionar esta oferta? Podés cambiarla antes de cerrar la subasta.",
      labelConfirmar: "Adjudicar",
    },
    rechazar: {
      titulo: "Rechazar oferta",
      mensaje:
        "¿Querés rechazar esta oferta? Esta acción no se puede deshacer.",
      labelConfirmar: "Rechazar",
    },
    cancelar: {
      titulo: "Cancelar subasta",
      mensaje:
        "¿Querés cancelar esta subasta? Todas las ofertas serán rechazadas. Esta acción no se puede deshacer.",
      labelConfirmar: "Cancelar subasta",
    },
    cerrar: {
      titulo: "Cerrar subasta",
      mensaje:
        "¿Querés cerrar esta subasta? La oferta seleccionada será aceptada y el resto rechazadas.",
      labelConfirmar: "Cerrar subasta",
    },
  };

  const config = modal ? MODAL_CONFIG[modal.tipo] : null;

  return (
    <>
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
                {figuritaSubastada.jugador}
              </p>
              <p className="mb-0 text-muted" style={{ fontSize: "0.75rem" }}>
                {figuritaSubastada.seleccion?.nombre} · #
                {figuritaSubastada.numero}
              </p>
            </div>
          </div>
          <span
            className={`badge rounded-pill px-2 py-1 ${badge.className}`}
            style={{ fontSize: "0.72rem", fontWeight: 500 }}
          >
            {badge.label}
          </span>
        </div>

        {/* Tiempo + cantidad ofertas */}
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
            <strong>{cantidadOfertas}</strong>{" "}
            {cantidadOfertas === 1 ? "oferta recibida" : "ofertas recibidas"}
          </span>
        </div>

        {/* Ofertas activas */}
        {!finalizada && (
          <div className="px-3 py-2 d-flex flex-column gap-2 border-top">
            <p className="mb-0 text-muted" style={{ fontSize: "0.78rem" }}>
              Ofertas recibidas
            </p>

            {ofertas?.length > 0 ? (
              ofertas.map((oferta) => (
                <div
                  key={oferta.id}
                  className="d-flex align-items-center gap-2 rounded-3 px-2 py-2"
                  style={{
                    backgroundColor: oferta.seleccionada
                      ? "#f0f9f4"
                      : "#fafafa",
                  }}
                >
                  {/* Avatar */}
                  <div
                    className="d-flex align-items-center justify-content-center rounded-circle flex-shrink-0"
                    style={{
                      width: 32,
                      height: 32,
                      backgroundColor: "#d9ead3",
                      fontSize: "0.7rem",
                      fontWeight: 700,
                    }}
                  >
                    {oferta.iniciales}
                  </div>

                  {/* Info oferta */}
                  <div className="flex-grow-1 overflow-hidden">
                    <p
                      className="mb-0 fw-semibold"
                      style={{ fontSize: "0.82rem" }}
                    >
                      {oferta.usuario}
                      <span
                        className="ms-1 text-warning"
                        style={{ fontSize: "0.72rem" }}
                      >
                        ★ {oferta.calificacion.toFixed(1)}
                      </span>
                    </p>
                    <p
                      className="mb-0 text-muted text-truncate"
                      style={{ fontSize: "0.75rem" }}
                    >
                      {oferta.label}
                    </p>
                  </div>

                  {/* Badge seleccionada */}
                  {oferta.seleccionada && (
                    <span
                      className="badge text-success bg-success-subtle px-2"
                      style={{ fontSize: "0.7rem" }}
                    >
                      Seleccionada
                    </span>
                  )}

                  {/* Acciones */}
                  {!oferta.seleccionada && (
                    <button
                      className="btn btn-outline-secondary btn-sm"
                      style={{ fontSize: "0.78rem" }}
                      onClick={() =>
                        setModal({ tipo: "adjudicar", ofertaId: oferta.id })
                      }
                    >
                      Adjudicar
                    </button>
                  )}
                  <button
                    className="btn btn-outline-secondary btn-sm px-2"
                    onClick={() =>
                      setModal({ tipo: "rechazar", ofertaId: oferta.id })
                    }
                  >
                    ✕
                  </button>
                </div>
              ))
            ) : (
              <p
                className="mb-0 text-muted text-center py-2"
                style={{ fontSize: "0.82rem" }}
              >
                Todavía no recibiste ofertas para esta subasta.
              </p>
            )}
          </div>
        )}

        {/* Ganador — solo finalizada */}
        {finalizada && ganador && (
          <div className="px-3 py-2 border-top" style={{ fontSize: "0.82rem" }}>
            <span className="text-muted">Ganador: </span>
            <strong>{ganador.usuario}</strong>
            <span className="text-muted"> · {ganador.label}</span>
          </div>
        )}

        {/* Acciones */}
        <div className="px-3 py-2 d-flex gap-2 border-top">
          {finalizada ? (
            <>
              <button
                className="btn btn-outline-secondary flex-fill"
                style={{ fontSize: "0.85rem" }}
                onClick={onVerResumen}
              >
                Ver resumen
              </button>
              {ganador && !yaCalificado &&(
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
              <button
                className="btn btn-outline-secondary flex-fill"
                style={{ fontSize: "0.85rem" }}
                onClick={onVerDetalle}
              >
                Ver detalle
              </button>
              <button
                className="btn btn-outline-danger flex-fill"
                style={{ fontSize: "0.85rem" }}
                onClick={() => setModal({ tipo: "cancelar" })}
              >
                Cancelar subasta
              </button>
              {haySeleccionada && (
                <button
                  className="btn btn-success flex-fill"
                  style={{ fontSize: "0.85rem" }}
                  onClick={() => setModal({ tipo: "cerrar" })}
                >
                  Cerrar subasta
                </button>
              )}
            </>
          )}
        </div>
      </div>

      {/* Modal confirmación */}
      <ConfirmModal
        show={modal !== null}
        titulo={config?.titulo}
        mensaje={config?.mensaje}
        labelConfirmar={loadingModal ? "Cargando..." : config?.labelConfirmar}
        onConfirmar={handleConfirmar}
        onCancelar={() => setModal(null)}
      />

      {/* Modal calificación */}
      <CalificarModal
        show={mostrarCalificar}
        usuario={ganador?.usuario}
        onConfirmar={handleCalificar}
        onCancelar={() => setMostrarCalificar(false)}
      />
    </>
  );
};

export default MiSubastaCard;
