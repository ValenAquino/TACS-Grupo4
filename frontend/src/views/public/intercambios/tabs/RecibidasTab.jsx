import { useState, useEffect } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import {aceptarIntercambio,rechazarIntercambio} from "../../../../services/intercambioService.js";

const RecibidasTab = ({ pendientes }) => {
    const [selected, setSelected] = useState(null);
    const [lista, setLista] = useState(pendientes);

    useEffect(() => {
      setLista(pendientes);
    }, [pendientes]);

    const handleAceptar = async (id) => {
      await aceptarIntercambio(id);
      setLista(prev => prev.filter(i => i.id !== id));
      setSelected(null);
    };

    const handleRechazar = async (id) => {
      await rechazarIntercambio(id);
      setLista(prev => prev.filter(i => i.id !== id));
      setSelected(null);
    };

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

            <h6 className="fw-bold mt-3">PENDIENTES ({lista.length})</h6>

            {lista.map(i => (
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
                            <button
                              className="btn btn-sm flex-fill btn-aceptar"
                              onClick={() => handleAceptar(i.id)}
                            >
                              ✓ Aceptar
                            </button>

                            <button
                              className="btn btn-sm flex-fill btn-rechazar"
                              onClick={() => handleRechazar(i.id)}
                            >
                              ✗ Rechazar
                            </button>
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
                        <button
                          className="btn btn-sm flex-fill btn-aceptar"
                          onClick={() => handleAceptar(selected.id)}
                        >
                          ✓ Aceptar
                        </button>
                        <button className="btn btn-sm flex-fill btn-rechazar"
                        onClick={() => handleRechazar(selected.id)}
                        >
                        ✗ Rechazar
                        </button>
                    </>
                }
            />

        </div>
    );
};

export default RecibidasTab;