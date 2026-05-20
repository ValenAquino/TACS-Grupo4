import { useEffect, useState } from "react";
import { buscarRepetidas } from "@/services/coleccionService.js";
import RepetidaCard from "../../../../../components/ui/repetida-card/repetida-card.jsx";
import FilterChip from "../../../../../components/ui/filter-chip/filter-chip.jsx";
import Button from "../../../../../components/ui/button/button.jsx";
import { useNavigate } from "react-router";
import Paginacion from "../../../../../components/ui/paginacion/paginacion.jsx";
import {useAuth} from "@/contexts/userContext.jsx";
import {useError} from "@/contexts/errorContext.jsx";

const Repetidas = () => {
    const [repetidas, setRepetidas] = useState({});
    const [filtros, setFiltros] = useState({
        metodoIntercambio: "",
    });

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [pagina, setPagina] = useState(1);

    const { user } = useAuth()
    const {handleError} = useError()

    const coleccionId = user?.colId

    const navigate = useNavigate();

    useEffect(() => {
        const cargarRepetidas = async () => {
            try {
                setLoading(true);

                const repetidasApi = await buscarRepetidas({
                    ...filtros,
                    pagina,
                    limite: 10,
                });

                setRepetidas(repetidasApi);
            } catch (err) {
                handleError(err, () => {});
            } finally {
                setLoading(false);
            }
        };

        cargarRepetidas();
    }, [coleccionId, filtros, pagina]);

    const cambiarFiltro = (nuevoTipo) => {
        setFiltros((prev) => {
            if (prev.metodoIntercambio === nuevoTipo) return prev;

            return {
                ...prev,
                metodoIntercambio: nuevoTipo,
            };
        });

        setPagina(1);
    };

    if (error) {
        return (
            <div className="text-center text-danger py-4">
                Error al cargar repetidas
            </div>
        );
    }

    return (
        <div className="container-fluid px-0 d-flex flex-column gap-4">

            <div className="row g-3 justify-content-center">
                <div className="col-6 col-md-4">
                    <div
                        className="border rounded-4 p-4 text-center shadow-sm h-100"
                        style={{ backgroundColor: "var(--color-primary)" }}
                    >
                        <p className="mb-1 fw-bold fs-2">
                            {repetidas.publicadas ?? 0}
                        </p>
                        <p className="mb-0 text-muted">Publicadas</p>
                    </div>
                </div>

                <div className="col-6 col-md-4">
                    <div
                        className="border rounded-4 p-4 text-center shadow-sm h-100"
                        style={{ backgroundColor: "var(--color-primary)" }}
                    >
                        <p className="mb-1 fw-bold fs-2">
                            {repetidas.disponibles ?? 0}
                        </p>
                        <p className="mb-0 text-muted">Disponibles</p>
                    </div>
                </div>
            </div>

            <div className="d-flex flex-wrap justify-content-start gap-2">
                <FilterChip
                    label="Todas"
                    selected={
                        filtros.metodoIntercambio ===
                        ""
                    }
                    onClick={() =>
                        cambiarFiltro("")
                    }
                />

                <FilterChip
                    label="Intercambio"
                    selected={
                        filtros.metodoIntercambio === "INTERCAMBIO"
                    }
                    onClick={() => cambiarFiltro("INTERCAMBIO")}
                />

                <FilterChip
                    label="Subasta"
                    selected={
                        filtros.metodoIntercambio === "SUBASTA"
                    }
                    onClick={() => cambiarFiltro("SUBASTA")}
                />
            </div>

            <div className="d-flex justify-content-between align-items-center gap-3 flex-nowrap">
                <p className="mb-0">
                    {repetidas.resultados ?? 0} resultados encontrados
                </p>

                <div className="flex-shrink-0">
                    <Button
                        label="Agregar repetida ↗"
                        onClick={() =>
                            navigate("/mis-figuritas/nueva-repetida")
                        }
                    />
                </div>
            </div>

            {loading ? (
                <div className="row g-3 justify-content-center">
                    {[...Array(8)].map((_, i) => (
                        <div
                            key={i}
                            className="col-6 col-md-4 col-lg-3"
                        >
                            <div
                                className="rounded-4 placeholder-glow border"
                                style={{ height: "220px" }}
                            >
                                <div className="placeholder w-100 h-100 rounded-4"></div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <>
                    <div className="row g-3 justify-content-center">
                        {repetidas?.data?.length > 0 ? (
                            repetidas.data.map((fig) => (
                                <div
                                    key={fig.figurita_id}
                                    className="col-6 col-md-4 col-lg-3 d-flex justify-content-center"
                                >
                                    <RepetidaCard figurita={fig} />
                                </div>
                            ))
                        ) : (
                            <div className="col-12 text-center text-muted py-5">
                                <div className="fs-1">📭</div>
                                <p className="mb-0">
                                    No hay resultados...
                                </p>
                            </div>
                        )}
                    </div>

                    <div className="pt-3 d-flex justify-content-center">
                        <Paginacion
                            page={pagina}
                            totalPages={
                                repetidas.paginas_totales ?? 1
                            }
                            onChange={setPagina}
                        />
                    </div>
                </>
            )}
        </div>
    );
};

export default Repetidas;