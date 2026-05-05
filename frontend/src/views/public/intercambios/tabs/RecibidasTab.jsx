import { useState } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";

const RecibidasTab = ({ pendientes }) => {
    const [selected, setSelected] = useState(null);

    return (
        <div>

            {/* ESTILOS */}
                        <style>{`
                            .btn-aceptar {
                              border: 1px solid #175A2D;
                              color: #175A2D;
                              background-color: transparent;
                            }

                            .btn-aceptar:hover {
                              background-color: #175A2D;
                              color: white;
                            }

                            .btn-rechazar {
                              border: 1px solid #dc3545;
                              color: #dc3545;
                              background-color: transparent;
                            }

                            .btn-rechazar:hover {
                              background-color: #dc3545;
                              color: white;
                            }
                        `}</style>

            <h6 className="fw-bold mt-3">PENDIENTES ({pendientes.length})</h6>

            {pendientes.map(i => (
                <IntercambioCard
                    key={i.id}
                    intercambio={i}
                    izquierda="pide"
                    derecha="ofrece"
                    labelIzq="Te pide"
                    labelDer="Te ofrece"
                    badge={i.esNueva && { etiqueta: "Nueva", color: "warning" }}
                    botones={
                        <>
                            <button className="btn btn-sm flex-fill btn-aceptar">✓ Aceptar</button>
                            <button className="btn btn-sm flex-fill btn-rechazar">✗ Rechazar</button>
                            <button onClick={() => setSelected(i)} className="btn btn-outline-dark btn-sm flex-fill">
                                Ver más
                            </button>
                        </>
                    }
                />
            ))}

            <IntercambioModal
                selected={selected}
                onClose={() => setSelected(null)}
                izquierda="pide"
                derecha="ofrece"
                labelIzq="Te pide"
                labelDer="Te ofrece"
                extraBotones={
                    <>
                        <button className="btn btn-sm flex-fill btn-aceptar">✓ Aceptar</button>
                        <button className="btn btn-sm flex-fill btn-rechazar">✗ Rechazar</button>
                    </>
                }
            />

        </div>
    );
};

export default RecibidasTab;