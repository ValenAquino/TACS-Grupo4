import { useEffect, useState } from "react";
import { buscarFaltantes } from "@/services/coleccionService.js";
import FaltanteCard from "../../../../../components/ui/faltante-card/faltante-card.jsx";
import Paginacion from "../../../../../components/ui/paginacion/paginacion.jsx";
import { useNavigate } from "react-router";
import Button from "../../../../../components/ui/button/button.jsx";
import {useError} from "@/contexts/errorContext.jsx";

const Faltantes = () => {

    const {handleError} = useError()

    const [faltantes, setFaltantes] = useState([]);
    const [filtros, setFiltros] = useState({});
    const [loading, setLoading] = useState(true);
    const [pagina, setPagina] = useState(1);

    const navigate = useNavigate();

    useEffect(() => {
        const cargarFaltantes = async () => {
            try {
                setLoading(true);

                const faltantesApi = await buscarFaltantes({
                    ...filtros,
                    pagina,
                    limite: 10,
                });

                setFaltantes(faltantesApi);
            } catch (err) {
                handleError(err, () => {});
            } finally {
                setLoading(false);
            }
        };

        cargarFaltantes();
    }, [filtros, pagina]);

    return (
        <div className="container-fluid px-0 d-flex flex-column gap-4">

            <div className="row justify-content-center">
                <div className="col-12 col-sm-8 col-md-6 col-lg-4">
                    <div
                        className="border rounded-4 p-4 text-center shadow-sm"
                        style={{ backgroundColor: "var(--color-primary)" }}
                    >
                        <p className="mb-1 fw-bold fs-2">
                            {faltantes.resultados ?? 0}
                        </p>
                        <p className="mb-0 text-muted">Me Faltan</p>
                    </div>
                </div>
            </div>

            <div className="d-flex justify-content-between align-items-center gap-3 flex-nowrap">
                <p className="mb-0">
                    {faltantes.resultados ?? 0} resultados encontrados
                </p>

                <div className="flex-shrink-0">
                    <Button
                        label="Agregar faltante ↗"
                        onClick={() => navigate("/mis-figuritas/nueva-faltante")}
                    />
                </div>
            </div>

            {loading ? (
                <div className="row g-3 justify-content-center">
                    {[...Array(10)].map((_, i) => (
                        <div key={i} className="col-6 col-md-4 col-lg-3">
                            <div
                                className="rounded-4 placeholder-glow border"
                                style={{
                                    height: "220px",
                                }}
                            >
                                <div className="placeholder w-100 h-100 rounded-4"></div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <>
                    <div className="row g-3 justify-content-center">
                        {faltantes?.data?.length > 0 ? (
                            faltantes.data.map((fig) => (
                                <div
                                    key={fig.id}
                                    className="col-6 col-md-4 col-lg-3 d-flex justify-content-center"
                                >
                                    <FaltanteCard figurita={fig} />
                                </div>
                            ))
                        ) : (
                            <div className="col-12 text-center text-muted py-5">
                                <div className="fs-1">📭</div>
                                <p className="mb-0">No hay resultados...</p>
                            </div>
                        )}
                    </div>

                    <div className="pt-3 d-flex justify-content-center">
                        <Paginacion
                            page={pagina}
                            totalPages={faltantes.paginas_totales ?? 1}
                            onChange={setPagina}
                        />
                    </div>
                </>
            )}
        </div>
    );
};

export default Faltantes;