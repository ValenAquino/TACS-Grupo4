import { useEffect, useState } from "react";
import { buscarIntercambios } from "../../../services/intercambioService.js";
import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";
import RecibidasTab from "./tabs/RecibidasTab.jsx";
import EnviadasTab from "./tabs/EnviadasTab.jsx";
import HistorialTab from "./tabs/HistorialTab.jsx";

const Intercambios = () => {

    const [data, setData] = useState({
        pendientes: [],
        enviadas: [],
        historial: []
    });

    const tabs = [
        {
            key: "recibidas",
            label: `Recibidas (${data.pendientes.length})`,
            component: RecibidasTab,
            props: { pendientes: data.pendientes },
        },
        {
            key: "enviadas",
            label: "Enviadas",
            component: EnviadasTab,
            props: { esperando: data.enviadas },
        },
        {
            key: "historial",
            label: "Historial",
            component: HistorialTab,
            props: { intercambios: data.historial },
        },
    ];

    return (
        <div className="container py-3" style={{ maxWidth: 900 }}>

        <style>
            {`
            .tabs-fullwidth .nav {
              display: flex;
              width: 100%;
              background-color: #e9f5ee;
              border-radius: 8px;
              overflow: hidden;
            }

            .tabs-fullwidth .nav-item {
              flex: 1;
            }

            .tabs-fullwidth .nav-link {
              width: 100%;
              text-align: center;
              color: #175A2D;
              font-weight: 600;
              border-radius: 0;
              transition: all 0.2s ease;
            }

            /* TAB ACTIVO */
            .tabs-fullwidth .nav-link.active {
              background-color: #175A2D;
              color: white;
              border-color: #175A2D;
            }

            /* HOVER */
            .tabs-fullwidth .nav-link:hover {
              background-color: #1f6b3a;
              color: white;
            }
            `}
        </style>
            <h2
              className="fw-bold text-center mb-4"
              style={{ color: "#175A2D" }}
            >
              Intercambios
            </h2>
            <div className="tabs-fullwidth">
              <TabsContainer tabs={tabs} />
            </div>
        </div>
    );
};

export default Intercambios;