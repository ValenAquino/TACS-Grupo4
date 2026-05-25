import { useEffect, useState } from "react";
import IntercambioCard from "../../../../components/ui/intercambio-card/intercambio-card.jsx";
import IntercambioModal from "../../../../components/ui/intercambio-modal/intercambio-modal.jsx";
import { buscarPropuestas } from "@/services/propuestasService.js";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";
import FilterChip from "@/components/ui/filter-chip/filter-chip.jsx";
import { useError } from "@/contexts/errorContext.jsx";
import { useAuth } from "@/contexts/userContext.jsx";

const EnviadasTab = () => {
  const [selected, setSelected] = useState(null);
  const [enviadas, setEnviadas] = useState([]);
  const [filtros, setFiltros] = useState({
    estado: "",
    tipo: "ENVIADAS",
  });
  const [loading, setLoading] = useState(true);
  const [pagina, setPagina] = useState(1);

  const { handleError } = useError();
  const { user } = useAuth();

  const cambiarFiltro = (nuevoEstado) => {
    setFiltros((prev) => {
      if (prev.estado === nuevoEstado) return prev;
      return { ...prev, estado: nuevoEstado };
    });
    setPagina(1);
  };

  const cargarEnviadas = async () => {
    try {
      setLoading(true);
      const res = await buscarPropuestas({ pagina, limite: 10, ...filtros });
      setEnviadas(res);
    } catch (error) {
      handleError(error, () => {});
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarEnviadas();
  }, [pagina, filtros]);

  const textosEstado = {
    "": "Todas las propuestas",
    PENDIENTE: "Esperando respuesta",
    ACEPTADO: "Propuestas aceptadas",
    RECHAZADO: "Propuestas rechazadas",
    CANCELADO: "Propuestas canceladas",
  };

  const textoResultados = textosEstado[filtros.estado] || "Propuestas";

  return (
    <div className="container-fluid px-0 d-flex flex-column gap-4">
      <div className="d-flex flex-wrap gap-2">
        <FilterChip label="Todas" selected={filtros.estado === ""} onClick={() => cambiarFiltro("")} />
        <FilterChip label="Aceptadas" selected={filtros.estado === "ACEPTADO"} onClick={() => cambiarFiltro("ACEPTADO")} />
        <FilterChip label="Rechazadas" selected={filtros.estado === "RECHAZADO"} onClick={() => cambiarFiltro("RECHAZADO")} />
        <FilterChip label="Pendientes" selected={filtros.estado === "PENDIENTE"} onClick={() => cambiarFiltro("PENDIENTE")} />
        <FilterChip label="Canceladas" selected={filtros.estado === "CANCELADO"} onClick={() => cambiarFiltro("CANCELADO")} />
      </div>

      {loading ? (
        <div className="d-flex flex-column gap-3">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="rounded-3 placeholder-glow border" style={{ height: "140px" }}>
              <div className="placeholder w-100 h-100 rounded-3" />
            </div>
          ))}
        </div>
      ) : enviadas?.contenido?.length > 0 ? (
        <>
          <p className="mb-0">
            {textoResultados} ({enviadas.cantidad_de_elementos})
          </p>
          <div className="d-flex flex-column gap-3">
            {enviadas.contenido.map((i) => (
              <IntercambioCard
                key={i.id}
                intercambio={i}
                tipo="ENVIADA"
                onActualizado={cargarEnviadas}
              />
            ))}
          </div>
          <div className="pt-3 d-flex justify-content-center">
            <Paginacion
              page={pagina}
              totalPages={enviadas.cantidad_de_paginas ?? 1}
              onChange={setPagina}
            />
          </div>
        </>
      ) : (
        <div className="text-center text-muted py-5">
          <div className="fs-1">📭</div>
          <p className="mb-0">No hay resultados...</p>
        </div>
      )}

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