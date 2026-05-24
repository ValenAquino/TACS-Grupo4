import {useEffect, useState} from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import {buscarPropuestas} from "@/services/propuestasService.js";
import useUsuarioActual from "@/hooks/useUsuarioActual.js";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";
import FilterChip from "@/components/ui/filter-chip/filter-chip.jsx";
import {useError} from "@/contexts/errorContext.jsx";
import {useAuth} from "@/contexts/userContext.jsx";

 const EnviadasTab = () => {
    const [selected, setSelected] = useState(null);
    const [enviadas, setEnviadas] = useState([]);
    const [filtros, setFiltros] = useState({
        estado: "",
        tipo: "ENVIADAS"
    });
    const [loading, setLoading] = useState(true);
    const [pagina, setPagina] = useState(0);

    const {handleError} = useError()

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

     const cargarEnviadas = async () => {
         try {
             setLoading(true);

             console.log("FILTROS:", filtros);

             const enviadasApi = await buscarPropuestas({
                 pagina: pagina,
                 limite: 10,
                 ...filtros
             });

             console.log("RESPUESTA:", enviadasApi);

             setEnviadas(enviadasApi);

         } catch (error) {
             console.error(error);
             handleError(error, () => {})
         } finally {
             setLoading(false);
         }
     }

    useEffect(() => {
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
                        filtros.estado === "RECHAZADO"
                    }
                    onClick={() => cambiarFiltro("RECHAZADO")}
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
                enviadas?.contenido?.length > 0 ?
                <>
                    <p className={"mb-3"}>Esperando Respuesta {`(${enviadas.cantidad_de_elementos})`}</p>
                    {enviadas.contenido.map(i => (
                        <IntercambioCard
                            key={i.id}
                            intercambio={i}
                            tipo={"ENVIADA"}
                            onActualizado={cargarEnviadas}
                        />
                    ))}
                </> : (
                        <div className="col-12 text-center text-muted py-5">
                            <div className="fs-1">📭</div>
                            <p className="mb-0">No hay resultados...</p>
                        </div>
                )}

            <Paginacion
                page={pagina}
                totalPages={enviadas.cantidad_de_paginas ?? 1}
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