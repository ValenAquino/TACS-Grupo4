import { useEffect, useState } from "react";
import { buscarMisSubastas } from "../../../../../services/subastasService.js";
import MiSubastaCard from "../../../../../components/ui/subasta-card/mi-subasta-card.jsx";
import Button from "../../../../../components/ui/button/button.jsx";
import { useNavigate } from "react-router";
import useUsuarioActual from "../../../../../hooks/useUsuarioActual.js";

const MisSubastas = () => {
  const [data, setData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [refresh, setRefresh] = useState(0);
  const navigate = useNavigate();
  const {userId} = useUsuarioActual()

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true);
        const res = await buscarMisSubastas(userId);
        console.log(res);
        setData(res);
      } catch {
        setError(true);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [refresh]);

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
          onClick={() => navigate("/subastas/crear")}
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
                  onVerResumen={() => navigate(`/subastas/${sub.id}/resumen`)}
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
                  onVerDetalle={() => navigate(`/subastas/${sub.id}`)}
                  onVerResumen={() => navigate(`/subastas/${sub.id}/resumen`)}
                  onRefresh={() => setRefresh((r) => r + 1)}
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
        </>
      )}
    </div>
  );
};

export default MisSubastas;