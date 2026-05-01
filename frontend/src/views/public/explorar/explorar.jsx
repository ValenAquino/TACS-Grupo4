import { useState } from 'react';
import useFiguritas from '@/hooks/useFiguritas';
import ExplorarSearch from './search/explorar-search';
import SugerenciasBanner from './sugerencias-banner/sugerencias-banner';
import ExplorarFiltros from './filtros/explorar-filtros';
import ExplorarResultados from './resultados/explorar-resultados';
import styles from './explorar.module.css';

const Explorar = () => {
  const [jugador, setjugador] = useState('');
  const [seleccion, setSeleccion] = useState('');
  const [numero, setNumero] = useState('');
  const [tipo, setTipo] = useState('todos');
  const [ordenar, setOrdenar] = useState('recientes');

  const { figuritas, loading, error } = useFiguritas(jugador, seleccion, numero, tipo);

  const handleAction = (fig) => {
    console.warn('acción pendiente de implementar', fig.id);
  };

  return (
    <main className={styles.page}>
      <ExplorarSearch query={jugador} onQueryChange={setjugador} />
      <div className={styles.container}>
        <SugerenciasBanner />
        <ExplorarFiltros
          tipo={tipo}
          onTipoChange={setTipo}
          seleccion={seleccion}
          onSeleccionChange={setSeleccion}
          numero={numero}
          onNumeroChange={setNumero}
        />
        <ExplorarResultados
          figuritas={figuritas}
          ordenar={ordenar}
          onOrdenarChange={setOrdenar}
          onAction={handleAction}
          loading={loading}
          error={error}
        />
      </div>
    </main>
  );
};

export default Explorar;
