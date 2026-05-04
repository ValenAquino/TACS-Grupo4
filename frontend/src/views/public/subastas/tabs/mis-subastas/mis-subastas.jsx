import { useEffect, useState } from "react";
import { buscarMisSubastas } from "../../../../../services/subastasService.js";
import MiSubastaCard from "../../../../../components/ui/subasta-card/mi-subasta-card.jsx";
import Button from "../../../../../components/ui/button/button.jsx";
import Paginacion from "../../../../../components/ui/paginacion/paginacion.jsx";
import { useNavigate } from "react-router";

const MisSubastas = () => {
  const [data, setData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [pagina, setPagina] = useState(1);
  const [refresh, setRefresh] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true);
        const res = await buscarMisSubastas({ pagina, limite: 10 });
        setData(res);
      } catch {
        setError(true);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [pagina, refresh]);

  if (error)
    return (
      <div className="text-center text-danger py-4">
        Error al cargar subastas
      </div>
    );

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      <div className="d-flex justify-content-end">
        <Button
          label="Crear subasta ↗"
          onClick={() => navigate("/subastas/nueva")}
        />
      </div>

      {loading ? (
        <div className="d-flex flex-column gap-3">
          {[...Array(3)].map((_, i) => (
            <div
              key={i}
              className="rounded-3 placeholder-glow border"
              style={{ height: "180px" }}
            >
              <div className="placeholder w-100 h-100 rounded-3" />
            </div>
          ))}
        </div>
      ) : (
        <>
          {data.activas?.length > 0 && (
            <div className="d-flex flex-column gap-3">
              <p
                className="mb-0 fw-bold text-uppercase text-muted"
                style={{ fontSize: "0.8rem", letterSpacing: "0.08em" }}
              >
                Activas ({data.activas.length})
              </p>
              {data.activas.map((sub) => (
                <MiSubastaCard
                  key={sub.id}
                  subasta={sub}
                  onVerDetalle={() => navigate(`/subastas/${sub.id}`)}
                  onCancelar={() => navigate(`/subastas/${sub.id}/cancelar`)}
                  onCerrar={() => navigate(`/subastas/${sub.id}/cerrar`)}
                  onRefresh={() => setRefresh((r) => r + 1)}
                />
              ))}
            </div>
          )}

          {data.finalizadas?.length > 0 && (
            <div className="d-flex flex-column gap-3">
              <p
                className="mb-0 fw-bold text-uppercase text-muted"
                style={{ fontSize: "0.8rem", letterSpacing: "0.08em" }}
              >
                Finalizadas ({data.finalizadas.length})
              </p>
              {data.finalizadas.map((sub) => (
                <MiSubastaCard
                  key={sub.id}
                  subasta={sub}
                  onVerResumen={() => navigate(`/subastas/${sub.id}/resumen`)}
                  onCalificar={() => navigate(`/subastas/${sub.id}/calificar`)}
                />
              ))}
            </div>
          )}

          {!data.activas?.length && !data.finalizadas?.length && (
            <div className="text-center text-muted py-5">
              <div className="fs-1">📭</div>
              <p className="mb-0">No tenés subastas todavía</p>
            </div>
          )}

          <div className="pt-3 d-flex justify-content-center">
            <Paginacion
              page={pagina}
              totalPages={data.paginas_totales ?? 1}
              onChange={setPagina}
            />
          </div>
        </>
      )}
    </div>
  );
};

export default MisSubastas;
