import { useState } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import CalificarModal from "../../../../components/ui/calificar-modal/calificar-modal.jsx";
import { calificarPerfil } from "../../../../services/perfilService.js";
import useUsuarioActual from "../../../../hooks/useUsuarioActual.js";

const HistorialTab = ({ intercambios }) => {
    const [selected, setSelected] = useState(null);
    const [calificando, setCalificando] = useState(null);
    const { userId } = useUsuarioActual();

    return (
        <div>

            <h6 className="fw-bold mt-3">CONCRETADOS ({intercambios.length})</h6>

            {intercambios.map(i => (
                <IntercambioCard
                    key={i.id}
                    intercambio={i}
                    izquierda="recibiste"
                    derecha="entregaste"
                    labelIzq="Recibiste"
                    labelDer="Entregaste"
                    badge={{ etiqueta: "Concretado", color: "success" }}
                    botones={
                        <>
                            {!i.calificado && (
                                <button
                                    onClick={() => setCalificando(i)}
                                    className="btn btn-outline-warning btn-sm flex-fill"
                                >
                                    ★ Calificar usuario
                                </button>
                            )}

                            <button
                                onClick={() => setSelected(i)}
                                className="btn btn-outline-dark btn-sm flex-fill"
                            >
                                Ver detalle
                            </button>
                        </>
                    }
                />
            ))}

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
                        await calificarPerfil(
                            userId,
                            calificando.usuario.id,
                            {
                                valor: data.valor,
                                descripcion: data.descripcion,
                                transactionId: calificando.id,
                                tipoTransaccion: "INTERCAMBIO"
                            }
                        );

                        console.log("Calificación enviada");

                        setCalificando(null);
                    } catch (e) {
                        console.error("Error al calificar", e);
                    }
                }}
            />

        </div>
    );
};

export default HistorialTab;