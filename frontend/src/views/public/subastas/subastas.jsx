import SectionTitle from "../../../components/ui/section-title/section-title";
import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";
import MisSubastas from "./tabs/mis-subastas/mis-subastas.jsx";
import Participo from "./tabs/participo/participo.jsx";

const Subastas = () => {
  const TABS = [
    {
      key: "mis-subastas",
      label: "Mis subastas",
      component: MisSubastas,
      props: {},
    },
    { key: "participo", label: "Participo", component: Participo, props: {} },
  ];

  return (
    <main className="container py-4 px-3 px-md-4">
      <div className="mx-auto" style={{ maxWidth: "900px" }}>
        <div className="d-flex flex-column gap-4">
          <SectionTitle>Subastas</SectionTitle>
          <TabsContainer tabs={TABS} />
        </div>
      </div>
    </main>
  );
};

export default Subastas;
