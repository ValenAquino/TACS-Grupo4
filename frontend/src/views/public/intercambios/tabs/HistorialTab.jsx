import { useState } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import CalificarModal from "../../../../components/ui/calificar-modal/calificar-modal.jsx";
import { calificarPerfil } from "../../../../services/perfilService.js";
import useUsuarioActual from "../../../../hooks/useUsuarioActual.js";
import { useNavigate } from "react-router-dom";

const HistorialTab = ({ intercambios }) => {

    const [calificando, setCalificando] = useState(null);

    const { userId } = useUsuarioActual();

    const navigate = useNavigate();

    return (
        <div>

            <h6 className="fw-bold mt-3">
                CONCRETADOS ({intercambios.length})
            </h6>

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
                                onClick={() => navigate(`/intercambios/${i.id}`)}
                                className="btn btn-outline-dark btn-sm flex-fill"
                            >
                                Ver detalle
                            </button>

                        </>
                    }
                />
            ))}

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
                                tipoTransaccion: "INTERCAMBIO",
                            }
                        );

                        setCalificando(null);

                    } catch (e) {
                        console.log(e);
                    }
                }}
            />

        </div>
    );
};

export default HistorialTab;