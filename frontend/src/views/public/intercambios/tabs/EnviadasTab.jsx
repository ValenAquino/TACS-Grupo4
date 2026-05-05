import { useState } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";

 const EnviadasTab = ({ esperando }) => {
    const [selected, setSelected] = useState(null);

    return (
        <div>
            {/* ESTILOS */}
                        <style>{`
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

            <h6 className="fw-bold mt-3">ESPERANDO ({esperando.length})</h6>

            {esperando.map(i => (
                <IntercambioCard
                    key={i.id}
                    intercambio={i}
                    izquierda="pedis"
                    derecha="ofreces"
                    labelIzq="Vos pedís"
                    labelDer="Vos ofrecés"
                    badge={{ etiqueta: "Enviada", color: "primary" }}
                    botones={
                        <>
                            <button onClick={() => setSelected(i)} className="btn btn-outline-dark btn-sm flex-fill">
                                Ver detalle
                            </button>
                            <button className="btn btn-sm flex-fill btn-rechazar">
                                Cancelar
                            </button>
                        </>
                    }
                />
            ))}

            <IntercambioModal
                selected={selected}
                onClose={() => setSelected(null)}
                izquierda="pedis"
                derecha="ofreces"
                labelIzq="Vos pedís"
                labelDer="Vos ofrecés"
            />

        </div>
    );
};

export default EnviadasTab;