import { useState } from "react";
import IntercambioCard from "@/components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "@/components/ui/intercambio-modal/intercambio-modal.jsx";
import CalificarModal from "@/components/ui/calificar-modal/calificar-modal.jsx";
import Button from "@/components/ui/button/button.jsx";
import { calificarPerfil } from "@/services/perfilService.js";
import useUsuarioActual from "@/hooks/useUsuarioActual.js";

const HistorialTab = ({ intercambios }) => {
  const [selected, setSelected] = useState(null);
  const [calificando, setCalificando] = useState(null);
  const { userId } = useUsuarioActual();

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      <p className="fw-semibold text-uppercase mb-0" style={{ fontSize: "0.8rem", letterSpacing: "0.05em" }}>
        Concretados ({intercambios.length})
      </p>

      {intercambios.length > 0 ? (
        <div className="d-flex flex-column gap-3">
          {intercambios.map((i) => (
            <IntercambioCard
              key={i.id}
              intercambio={i}
              izquierda="recibiste"
              derecha="entregaste"
              labelIzq="Recibiste"
              labelDer="Entregaste"
              badge={{ etiqueta: "Concretado", color: "success" }}
              botones={
                <div className="d-flex gap-2">
                  {!i.calificado && (
                    <Button
                      label="★ Calificar usuario"
                      variante="secundarioBorde"
                      className="flex-fill"
                      onClick={() => setCalificando(i)}
                    />
                  )}
                  <Button
                    label="Ver detalle"
                    variante="secundarioBorde"
                    className="flex-fill"
                    onClick={() => setSelected(i)}
                  />
                </div>
              }
            />
          ))}
        </div>
      ) : (
        <div className="text-center text-muted py-5">
          <div className="fs-1">📭</div>
          <p className="mb-0">No tenés intercambios concretados</p>
        </div>
      )}

      <IntercambioModal
        selected={selected}
        onClose={() => setSelected(null)}
        izquierda="recibiste"
        derecha="entregaste"
        labelIzq="Recibiste"
        labelDer="Entregaste"
      />

      <CalificarModal
        show={!!calificando}
        usuario={calificando?.usuario?.nombre}
        onCancelar={() => setCalificando(null)}
        onConfirmar={async (data) => {
          try {
            await calificarPerfil(userId, calificando.usuario.id, {
              valor: data.valor,
              descripcion: data.descripcion,
              transactionId: calificando.id,
              tipoTransaccion: "INTERCAMBIO",
            });
            setCalificando(null);
          } catch (e) {
            /* empty */
          }
        }}
      />
    </div>
  );
};

export default HistorialTab;