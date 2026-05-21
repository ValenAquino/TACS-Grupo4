import { useEffect, useState } from "react";
import { buscarSubastasParticipo } from "../../../../../services/subastasService.js";
import SubastaCard from "../../../../../components/ui/subasta-card/subasta-card.jsx";
import { useNavigate } from "react-router";
import useUsuarioActual from "../../../../../hooks/useUsuarioActual.js";
import {useError} from "@/contexts/errorContext.jsx";

const Participo = () => {
  const [data, setData] = useState({});
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const {handleError} = useError();
  const {userId} = useUsuarioActual()

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true);
        const res = await buscarSubastasParticipo(userId);
        setData(res);
      } catch(error) {
        handleError(error, () => {});
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  const activasCount = data.activas?.length ?? 0;
  const seleccionadasCount =
    data.activas?.filter((s) => s.tuOferta?.estado === "SELECCIONADO").length ?? 0;
  const hayResultados = activasCount > 0 || (data.finalizadas?.length ?? 0) > 0;

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      {/* Stats */}
      <div className="row g-3 justify-content-center">
        <div className="col-6 col-md-4">
          <div
            className="border rounded-4 p-4 text-center shadow-sm h-100"
            style={{ backgroundColor: "var(--color-primary)" }}
          >
            <p className="mb-1 fw-bold fs-2">{activasCount}</p>
            <p className="mb-0 text-muted">Ofertas activas</p>
          </div>
        </div>
        <div className="col-6 col-md-4">
          <div
            className="border rounded-4 p-4 text-center shadow-sm h-100"
            style={{ backgroundColor: "var(--color-primary)" }}
          >
            <p className="mb-1 fw-bold fs-2">{seleccionadasCount}</p>
            <p className="mb-0 text-muted">Mejor oferta</p>
          </div>
        </div>
      </div>

      {loading ? (
        <div className="d-flex flex-column gap-3">
          {[...Array(3)].map((_, i) => (
            <div
              key={i}
              className="rounded-3 placeholder-glow border"
              style={{ height: "140px" }}
            >
              <div className="placeholder w-100 h-100 rounded-3" />
            </div>
          ))}
        </div>
      ) : !hayResultados ? (
        <div className="text-center text-muted py-5">
          <div className="fs-1">📭</div>
          <p className="mb-0">No participás en ninguna subasta todavía</p>
        </div>
      ) : (
        <>
          {data.activas?.length > 0 && (
            <div className="d-flex flex-column gap-3">
              <p
                className="mb-0 fw-bold text-uppercase text-muted"
                style={{ fontSize: "0.8rem" }}
              >
                Activas ({data.activas.length})
              </p>
              {data.activas.map((sub) => (
                <SubastaCard
                  key={sub.id}
                  subasta={sub}
                />
              ))}
            </div>
          )}

          {data.finalizadas?.length > 0 && (
            <div className="d-flex flex-column gap-3">
              <p
                className="mb-0 fw-bold text-uppercase text-muted"
                style={{ fontSize: "0.8rem" }}
              >
                Finalizadas ({data.finalizadas.length})
              </p>
              {data.finalizadas.map((sub) => (
                <SubastaCard
                  key={sub.id}
                  subasta={sub}
                  onVerSubasta={() => navigate(`/subastas/${sub.id}`)}
                  onMejorarOferta={() => navigate(`/subastas/${sub.id}/oferta`)}
                  onVerResumen={() => navigate(`/subastas/${sub.id}/resumen`)}
                />
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Participo;