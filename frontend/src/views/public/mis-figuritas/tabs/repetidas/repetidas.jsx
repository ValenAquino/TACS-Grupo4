import {useEffect, useState} from 'react';
import styles from './Repetidas.module.css';
import {buscarRepetidas} from "../../../../../services/coleccionService.js";
import RepetidaCard from "../../../../../components/ui/repetida-card/repetida-card.jsx";
import FilterChip from "../../../../../components/ui/filter-chip/filter-chip.jsx";
import Button from "../../../../../components/ui/button/button.jsx";
import {useNavigate} from "react-router";
import Paginacion from "../../../../../components/ui/paginacion/paginacion.jsx";

const Repetidas = ({colId}) => {

    const [repetidas, setRepetidas] = useState({});
    const [filtros, setFiltros] = useState({
        metodoIntercambio: "SUBASTA_E_INTERCAMBIO"
    });

    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [pagina, setPagina] = useState(1);

    useEffect(() => {
        const cargarRepetidas = async () => {
            try {
                setLoading(true)
                const repetidasApi = await buscarRepetidas(colId, {...filtros, pagina, limite: 10});
                setRepetidas(repetidasApi)
            } catch (error) {
                setError(true);
            } finally {
                setLoading(false)
            }
        }

        cargarRepetidas();
    }, [filtros, pagina])

  if (error) {
    return <p className={styles.error}>{error}</p>;
  }

    const cambiarFiltro = (nuevoTipo) => {
        setFiltros(prev => {
            if (prev.metodoIntercambio === nuevoTipo) return prev
            return {
                ...prev,
                metodoIntercambio: nuevoTipo
            }
        });
    };

    return (
        <div
            className="d-flex flex-column gap-4 h-100"
            style={{ minHeight: "100%" }}
        >

            <div className="row g-3">
                <div className="col-6">
                    <div
                        className="border rounded-3 p-3 text-center h-100"
                        style={{ backgroundColor: "var(--color-primary)" }}
                    >
                        <p className="mb-1 fw-bold fs-4">
                            {repetidas.publicadas ?? 0}
                        </p>
                        <p className="mb-0 text-muted">Publicadas</p>
                    </div>
                </div>

                <div className="col-6">
                    <div
                        className="border rounded-3 p-3 text-center h-100"
                        style={{ backgroundColor: "var(--color-primary)" }}
                    >
                        <p className="mb-1 fw-bold fs-4">
                            {repetidas.disponibles ?? 0}
                        </p>
                        <p className="mb-0 text-muted">Disponibles</p>
                    </div>
                </div>
            </div>

            <div className="d-flex flex-wrap gap-2">
                <FilterChip
                    label="Todas"
                    selected={filtros.metodoIntercambio === "SUBASTA_E_INTERCAMBIO"}
                    onClick={() => cambiarFiltro("SUBASTA_E_INTERCAMBIO")}
                />

                <FilterChip
                    label="Intercambio"
                    selected={filtros.metodoIntercambio === "INTERCAMBIO"}
                    onClick={() => cambiarFiltro("INTERCAMBIO")}
                />

                <FilterChip
                    label="Subasta"
                    selected={filtros.metodoIntercambio === "SUBASTA"}
                    onClick={() => cambiarFiltro("SUBASTA")}
                />
            </div>

            <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2">
                <p className="mb-0">
                    {repetidas.resultados ?? 0} resultados encontrados
                </p>

                <Button
                    label="Agregar repetida ↗"
                    onClick={() =>
                        navigate("/mis-figuritas/nueva-repetida")
                    }
                />
            </div>

            {loading ? (
                <div className="row g-3">
                    {[...Array(8)].map((_, i) => (
                        <div
                            key={i}
                            className="col-6 col-md-4 col-lg-3"
                        >
                            <div className={styles.skeleton} />
                        </div>
                    ))}
                </div>
            ) : (
                <>
                    <div className="row g-3 gap-3">
                        {repetidas?.data?.length > 0 ? (
                            repetidas.data.map((fig) => (
                                <div
                                    key={fig.figurita_id}
                                    className="col-6 col-md-4 col-lg-3"
                                >
                                    <RepetidaCard figurita={fig} />
                                </div>
                            ))
                        ) : (
                            <div className="col-12">
                                <div className="text-center text-muted py-5">
                                    <div className="fs-1">📭</div>
                                    <p className="mb-0">
                                        No hay resultados...
                                    </p>
                                </div>
                            </div>
                        )}
                    </div>

                    <div className="mt-auto pt-3">
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
