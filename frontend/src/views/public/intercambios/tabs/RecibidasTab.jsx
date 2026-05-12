import { useState, useEffect } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import {aceptarIntercambio,rechazarIntercambio} from "../../../../services/intercambioService.js";
import {buscarPropuestas} from "@/services/propuestasService.js";
import useUsuarioActual from "@/hooks/useUsuarioActual.js";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";

const RecibidasTab = () => {
    const [selected, setSelected] = useState(null);
    const [loading, setLoading] = useState(true);
    const [pagina, setPagina] = useState(1);
    const [error, setError] = useState(false);
    const [recibidas, setRecibidas] = useState([]);
    const [filtros, setFiltros] = useState({
        estado: "",
        tipo: "RECIBIDAS"
    });

    const { userId} = useUsuarioActual()
    const user_id = userId

    useEffect(() => {
        const cargarRecibidas = async () => {
            try {
                setLoading(true);
                const enviadasApi = await buscarPropuestas(user_id, {pagina: pagina, limite: 10, ...filtros})
                setRecibidas(enviadasApi)
            } catch (e) {
                setError(true)
            } finally {
                setLoading(false);
            }
        }

        cargarRecibidas();
    }, [pagina, filtros]);

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


            {loading ? <p>Cargando resultados...</p>
                :
                <>
                    <h6 className="fw-bold mt-3">PENDIENTES ({`${recibidas.resultados}`})</h6>
                    {recibidas.data.map(i => (
                        <IntercambioCard
                            key={i.id}
                            intercambio={i}
                        />
                    ))}
                </>
            }

            <Paginacion
                page={pagina}
                totalPages={recibidas.paginas_totales ?? 1}
                onChange={setPagina}
            />

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