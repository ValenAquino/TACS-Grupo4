import styles from './faltantes.module.css';
import {useEffect, useState} from "react";
import {buscarFaltantes} from "../../../../../services/coleccionService.js";
import FaltanteCard from "../../../../../components/ui/faltante-card.jsx";
import Paginacion from "../../../../../components/ui/paginacion/paginacion.jsx";
import {useNavigate} from "react-router";
import Button from "../../../../../components/ui/button/button.jsx";

// ── Component ─────────────────────────────────────────────────────────────────
const Faltantes = ({colId}) => {

    const [faltantes, setFaltantes] = useState([]);
    const [filtros, setFiltros] = useState({});

    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [pagina, setPagina] = useState(1);

    useEffect(() => {
        const cargarFaltantes = async (filtros = {}) => {
            try {
                setLoading(true)
                const faltantesApi = await buscarFaltantes(colId, {...filtros, pagina, limite: 10})
                setFaltantes(faltantesApi)
            } catch (error) {
                setError(true);
            } finally {
                setLoading(false)
            }
        }

        cargarFaltantes();
    }, [filtros, pagina]);

  if (error) {
    return <p className={styles.error}>{error}</p>;
  }

  return (
    <div className={styles.wrapper}>
        <div className="row g-3">
            <div className="col-6">
                <div
                    className="border rounded-3 p-3 text-center h-100"
                    style={{ backgroundColor: "var(--color-primary)" }}
                >
                    <p className="mb-1 fw-bold fs-4">
                        {faltantes.resultados ?? 0}
                    </p>
                    <p className="mb-0 text-muted">Me Faltan</p>
                </div>
            </div>
        </div>

        <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-2">
            <p className="mb-0">
                {faltantes.resultados ?? 0} resultados encontrados
            </p>

            <Button
                label="Agregar faltante ↗"
                onClick={() =>
                    navigate("/mis-figuritas/nueva-faltante")
                }
            />
        </div>

      {loading ? (
          <div className={`row g-3 ${styles.skeletonRow}`}>
              {[...Array(10)].map((_, i) => (
                  <div key={i} className="col-6 col-md-4 col-lg-3">
                      <div className={styles.skeleton} />
                  </div>
              ))}
          </div>
      ) : (
          <>
              <div className="row g-3">
                  {faltantes?.data?.length > 0 ? (
                      faltantes.data.map((fig) => (
                          <div key={fig.id} className="col-6 col-md-4 col-lg-3">
                              <FaltanteCard figurita={fig} />
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
                          faltantes.paginas_totales ?? 1
                      }
                      onChange={setPagina}
                  />
              </div>
          </>
      )}
    </div>
  )
}

export default Faltantes;
