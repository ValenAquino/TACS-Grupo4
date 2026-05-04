import { useEffect, useState } from "react";
import { buscarSubastasParticipo } from "../../../../../services/subastasService.js";
import SubastaCard from "../../../../../components/ui/subasta-card/subasta-card.jsx";
import FilterChip from "../../../../../components/ui/filter-chip/filter-chip.jsx";
import Paginacion from "../../../../../components/ui/paginacion/paginacion.jsx";
import { useNavigate } from "react-router";

const FILTROS = [
  { label: "Todas", value: "" },
  { label: "Activas", value: "activa" },
  { label: "Finaliza pronto", value: "finaliza_pronto" },
  { label: "Finalizadas", value: "finalizada" },
];

const Participo = () => {
  const [data, setData] = useState({});
  const [filtros, setFiltros] = useState({ estado: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [pagina, setPagina] = useState(1);
  const navigate = useNavigate();

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true);
        const res = await buscarSubastasParticipo({
          ...filtros,
          pagina,
          limite: 10,
        });
        setData(res);
      } catch {
        setError(true);
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [filtros, pagina]);

  const cambiarFiltro = (valor) => {
    setFiltros({ estado: valor });
    setPagina(1);
  };

  if (error)
    return (
      <div className="text-center text-danger py-4">
        Error al cargar subastas
      </div>
    );

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      {/* Stats */}
      <div className="row g-3 justify-content-center">
        <div className="col-6 col-md-4">
          <div
            className="border rounded-4 p-4 text-center shadow-sm h-100"
            style={{ backgroundColor: "var(--color-primary)" }}
          >
            <p className="mb-1 fw-bold fs-2">{data.activas_count ?? 0}</p>
            <p className="mb-0 text-muted">Ofertas activas</p>
          </div>
        </div>
        <div className="col-6 col-md-4">
          <div
            className="border rounded-4 p-4 text-center shadow-sm h-100"
            style={{ backgroundColor: "var(--color-primary)" }}
          >
            <p className="mb-1 fw-bold fs-2">{data.seleccionadas_count ?? 0}</p>
            <p className="mb-0 text-muted">Mejor Oferta</p>
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="d-flex flex-wrap gap-2">
        {FILTROS.map(({ label, value }) => (
          <FilterChip
            key={value}
            label={label}
            selected={filtros.estado === value}
            onClick={() => cambiarFiltro(value)}
          />
        ))}
      </div>

      <p className="mb-0">{data.resultados ?? 0} resultados encontrados</p>

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
      ) : (
        <>
          <div className="d-flex flex-column gap-3">
            {data.data?.length > 0 ? (
              data.data.map((sub) => (
                <SubastaCard
                  key={sub.id}
                  subasta={sub}
                  onVerSubasta={() => navigate(`/subastas/${sub.id}`)}
                  onMejorarOferta={() => navigate(`/subastas/${sub.id}/oferta`)}
                  onVerResumen={() => navigate(`/subastas/${sub.id}/resumen`)}
                />
              ))
            ) : (
              <div className="text-center text-muted py-5">
                <div className="fs-1">📭</div>
                <p className="mb-0">No hay resultados...</p>
              </div>
            )}
          </div>

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

export default Participo;
