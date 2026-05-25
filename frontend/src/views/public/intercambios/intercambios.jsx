import { useState } from 'react'
import TabsContainer from '../../../components/ui/tabs-container/tabs-container.jsx'
import RecibidasTab from './tabs/RecibidasTab.jsx'
import EnviadasTab from './tabs/EnviadasTab.jsx'
import SectionTitle from '../../../components/ui/section-title/section-title.jsx'

const Intercambios = () => {
  const [data] = useState({
    pendientes: [],
    enviadas: [],
    historial: [],
  })

  const tabs = [
    {
      key: 'recibidas',
      label: `Recibidas (${data.pendientes.length})`,
      component: RecibidasTab,
    },
    {
      key: 'enviadas',
      label: 'Enviadas',
      component: EnviadasTab,
    },
  ]

  return (
    <main className="container py-4 px-3 px-md-4">
      <div className="mx-auto" style={{ maxWidth: '900px' }}>
        <div className="d-flex flex-column gap-4">
          <SectionTitle>Intercambios</SectionTitle>
          <TabsContainer tabs={tabs} />
        </div>
      </div>
    </main>
  )
}

export default Intercambios
