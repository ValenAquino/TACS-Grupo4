import { useState, useEffect } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import {aceptarIntercambio,rechazarIntercambio} from "../../../../services/intercambioService.js";
import {buscarPropuestas} from "@/services/propuestasService.js";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";
import {useError} from "@/contexts/errorContext.jsx";
import FilterChip from "@/components/ui/filter-chip/filter-chip.jsx";

const RecibidasTab = () => {
    const [selected, setSelected] = useState(null);
    const [loading, setLoading] = useState(true);
    const [pagina, setPagina] = useState(1);
    const [recibidas, setRecibidas] = useState([]);
    const [filtros, setFiltros] = useState({
        estado: "",
        tipo: "RECIBIDAS"
    });

    const {handleError} = useError()

    const cargarRecibidas = async () => {
        try {
            setLoading(true);
            const enviadasApi = await buscarPropuestas({pagina: pagina, limite: 10, ...filtros})
            setRecibidas(enviadasApi)
        } catch (error) {
            handleError(error, () => {})
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
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

    const textosEstado = {
        "": "Todas las propuestas",
        "PENDIENTE": "Esperando respuesta",
        "ACEPTADO": "Propuestas aceptadas",
        "RECHAZADO": "Propuestas rechazadas",
        "CANCELADO": "Propuestas canceladas"
    };

    const textoResultados =
        textosEstado[filtros.estado] ||
        "Propuestas";

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

                <FilterChip
                    label="Canceladas"
                    selected={
                        filtros.estado === "CANCELADO"
                    }
                    onClick={() =>
                        cambiarFiltro("CANCELADO")
                    }
                />
            </div>

            {loading ? (
                <p>Cargando resultados...</p>
            ) : (
                <>
                    <p className={"mb-3"}>
                        {textoResultados} ({recibidas.cantidad_de_elementos})
                    </p>

                    {recibidas?.contenido?.length > 0 ? (
                        recibidas.contenido.map(i => (
                            <IntercambioCard
                                key={i.id}
                                intercambio={i}
                                tipo={"RECIBIDA"}
                                onActualizado={cargarRecibidas}
                            />
                        ))
                    ) : (
                        <div className="col-12 text-center text-muted py-5">
                            <div className="fs-1">📭</div>
                            <p className="mb-0">No hay resultados...</p>
                        </div>
                    )}
                </>
            )}

            <Paginacion
                page={pagina}
                totalPages={recibidas.cantidad_de_paginas ?? 1}
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