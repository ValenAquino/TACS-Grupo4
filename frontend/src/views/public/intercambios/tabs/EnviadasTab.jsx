import {useEffect, useState} from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import {buscarPropuestas} from "@/services/propuestasService.js";
import useUsuarioActual from "@/hooks/useUsuarioActual.js";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";
import FilterChip from "@/components/ui/filter-chip/filter-chip.jsx";

 const EnviadasTab = () => {
    const [selected, setSelected] = useState(null);
    const [enviadas, setEnviadas] = useState([]);
    const [filtros, setFiltros] = useState({
        estado: "",
        tipo: "ENVIADAS"
    });
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

     const cambiarFiltro = (nuevoEstado) => {
         setFiltros((prev) => {
             if (prev.estado === nuevoEstado) return prev;

             return {
                 ...prev,
                 estado: nuevoEstado,
             };
         });

         setPagina(1);
     };

    useEffect(() => {
        const cargarEnviadas = async () => {
            try {
                setLoading(true);
                const enviadasApi = await buscarPropuestas(user_id, {pagina: pagina, limite: 10, ...filtros})
                setEnviadas(enviadasApi)
            } catch (e) {
                setError(true)
            } finally {
                setLoading(false);
            }
        }

        cargarEnviadas();
    }, [pagina, filtros])

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

            <div className="d-flex flex-wrap justify-content-start gap-2 mb-3">
                <FilterChip
                    label="Todas"
                    selected={
                        filtros.estado ===
                        ""
                    }
                    onClick={() =>
                        cambiarFiltro("")
                    }
                />

                <FilterChip
                    label="Aceptadas"
                    selected={
                        filtros.estado === "ACEPTADO"
                    }
                    onClick={() => cambiarFiltro("ACEPTADO")}
                />

                <FilterChip
                    label="Rechazadas"
                    selected={
                        filtros.estado === "RECHAZO"
                    }
                    onClick={() => cambiarFiltro("RECHAZO")}
                />

                <FilterChip
                    label="Pendientes"
                    selected={
                        filtros.estado === "PENDIENTE"
                    }
                    onClick={() => cambiarFiltro("PENDIENTE")}
                />
            </div>

            {loading ? <p>Cargando resultados...</p>
                :
                <>
                    <p className={"mb-3"}>Esperando Respuesta {`(${enviadas.resultados})`}</p>
                    {enviadas.data.map(i => (
                        <IntercambioCard
                            key={i.id}
                            intercambio={i}
                            tipo={"ENVIADA"}
                        />
                    ))}
                </>
            }

            <Paginacion
                page={pagina}
                totalPages={enviadas.paginas_totales ?? 1}
                onChange={setPagina}
            />

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