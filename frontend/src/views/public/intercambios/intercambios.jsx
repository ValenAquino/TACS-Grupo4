import TabsContainer from '../../../components/ui/tabs-container/tabs-container.jsx'
import PropuestasTab from './tabs/propuestas-tab.jsx'
import SectionTitle from '../../../components/ui/section-title/section-title.jsx'

const tabs = [
  {
    key: 'recibidas',
    label: 'Recibidas',
    component: PropuestasTab,
    props: { tipo: 'RECIBIDAS', estadoInicial: 'PENDIENTE' },
  },
  { key: 'enviadas', label: 'Enviadas', component: PropuestasTab, props: { tipo: 'ENVIADAS', estadoInicial: 'PENDIENTE'} },
]
const Intercambios = () => (
  <main className="container py-4 px-3 px-md-4">
    <div className="mx-auto" style={{ maxWidth: '900px' }}>
      <div className="d-flex flex-column gap-4">
        <SectionTitle>Intercambios</SectionTitle>
        <TabsContainer tabs={tabs} />
      </div>
    </div>
  </main>
)

export default Intercambios
