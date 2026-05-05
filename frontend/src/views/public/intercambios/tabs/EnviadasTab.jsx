import {useEffect, useState} from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import { rechazarIntercambio } from "../../../../services/intercambioService.js";
import {buscarEnviadas} from "@/services/propuestasService.js";
import useUsuarioActual from "@/hooks/useUsuarioActual.js";

 const EnviadasTab = () => {
    const [selected, setSelected] = useState(null);
    const [enviadas, setEnviadas] = useState([]);
    const [filtros, setFiltros] = useState({});
    const [loading, setLoading] = useState(true);
    const [pagina, setPagina] = useState(1);
    const [error, setError] = useState(false);

    // const handleCancelar = async (id) => {
    //     try {
    //         await rechazarIntercambio(id);
    //         setLista(prev => prev.filter(i => i.id !== id));
    //     } catch (e) {
    //         console.error("Error al cancelar", e);
    //     }
    // };

    const {userId} = useUsuarioActual()
    const user_id = userId

    useEffect(() => {
        const cargarEnviadas = async () => {
            try {
                setLoading(true);
                const enviadasApi = await buscarEnviadas(user_id, {pagina: pagina, limite: 10, tipo: "ENVIADAS"})

                setEnviadas(enviadasApi)
            } catch (e) {
                setError(true)
            } finally {
                setLoading(false);
            }
        }

        cargarEnviadas();
    }, [])

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

            {loading ? <p>Cargando resultados...</p>
                :
                <>
                    {enviadas.data.map(i => (
                        <IntercambioCard
                            key={i.id}
                            intercambio={i}
                            izquierda="pedis"
                            derecha="ofreces"
                            labelIzq="Vos pedís"
                            labelDer="Vos ofrecés"
                            badge={{ etiqueta: "Enviada", color: "primary" }}
                            // botones={
                            //     <>
                            //         <button onClick={() => setSelected(i)} className="btn btn-outline-dark btn-sm flex-fill">
                            //             Ver detalle
                            //         {/*</button>*/}
                            //         {/*/!*<button*!/*/}
                            //         {/*/!*    className="btn btn-sm flex-fill btn-rechazar"*!/*/}
                            //         {/*/!*    onClick={() => handleCancelar(i.id)}*!/*/}
                            //         {/*/!*>*!/*/}
                            //         {/*    Cancelar*/}
                            //         {/*</button>*/}
                            //     </>
                            // }
                        />
                    ))}
                </>
            }



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