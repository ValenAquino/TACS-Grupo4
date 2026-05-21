import SectionTitle from "../../../components/ui/section-title/section-title";
import Repetidas from "./tabs/repetidas/repetidas";
import Faltantes from "./tabs/faltantes/faltantes";
import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";

const MisFiguritas = () => {
    const colId = "2";

    const TABS = [
        {
            key: "repetidas",
            label: "Repetidas",
            component: Repetidas
        },
        {
            key: "faltantes",
            label: "Faltantes",
            component: Faltantes
        },
    ];

    return (
        <main className="container py-4 px-3 px-md-4">
            <div className="mx-auto" style={{ maxWidth: "900px" }}>
                <div className="d-flex flex-column gap-4">
                    <SectionTitle>Mis figuritas</SectionTitle>
                    <TabsContainer tabs={TABS} />
                </div>
            </div>
        </main>
    );
};

export default MisFiguritas;
