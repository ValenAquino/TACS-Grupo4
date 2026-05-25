import { useState } from "react";
import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";
import RecibidasTab from "./tabs/RecibidasTab.jsx";
import EnviadasTab from "./tabs/EnviadasTab.jsx";
import HistorialTab from "./tabs/HistorialTab.jsx";
import "./intercambios.css";

const Intercambios = () => {

    const [data, setData] = useState({
        pendientes: [],
        enviadas: [],
        historial: []
    });

    const tabs = [
        {
            key: "recibidas",
            label: `Recibidas`,
            component: RecibidasTab,
        },
        {
            key: "enviadas",
            label: "Enviadas",
            component: EnviadasTab
        }
    ];

    return (
        <div className="container py-3 intercambios-container">

            <h2 className="fw-bold text-center mb-4 intercambios-title">
                Intercambios
            </h2>

            <div className="tabs-fullwidth">
                <TabsContainer tabs={tabs} />
            </div>

        </div>
    );
};

export default Intercambios;