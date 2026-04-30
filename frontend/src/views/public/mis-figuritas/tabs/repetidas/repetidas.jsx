import {useEffect, useState} from 'react';
import styles from './Repetidas.module.css';
import {buscarRepetidas} from "../../../../../services/coleccionService.js";
import RepetidaCard from "../../../../../components/ui/repetida-card/repetida-card.jsx";
import FilterChip from "../../../../../components/ui/filter-chip/filter-chip.jsx";

const Repetidas = ({colId}) => {

    const [repetidas, setRepetidas] = useState([]);
    const [filtros, setFiltros] = useState({
        tipo: "todas"
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(false);

    useEffect(() => {
        const cargarRepetidas = async () => {
            try {
                setLoading(true)
                const repetidasApi = await buscarRepetidas(colId, filtros)
                setRepetidas(repetidasApi.data)
            } catch (error) {
                setError(true);
            } finally {
                setLoading(false)
            }
        }

        cargarRepetidas();
    }, [filtros])

  if (error) {
    return <p className={styles.error}>{error}</p>;
  }

    const cambiarFiltro = (nuevoTipo) => {
        setFiltros(prev => {
            if (prev.tipo === nuevoTipo) return prev
            return {
                ...prev,
                tipo: nuevoTipo
            }
        });
    };

  return (
    <div className={styles.wrapper}>

        <div >

        </div>

      <div className="d-flex gap-1">
          <FilterChip
              label="Todas"
              selected={filtros.tipo === 'todas'}
              onClick={() => cambiarFiltro('todas')}
          />

          <FilterChip
              label="Intercambio"
              selected={filtros.tipo === 'intercambio'}
              onClick={() => cambiarFiltro('intercambio')}
          />

          <FilterChip
              label="Subasta"
              selected={filtros.tipo === 'subasta'}
              onClick={() => cambiarFiltro('subasta')}
          />
      </div>

      <div className={`row g-3`}>
          {repetidas.length > 0 ? repetidas.map((fig) => (
              <div key={fig.figurita_id} className="col-6 col-md-4 col-lg-3">
                  <RepetidaCard figurita={fig} />
              </div>
          )) : <h3>No hay resultados...</h3>}

      </div>

      {loading && (
        <div className={`row g-3 ${styles.skeletonRow}`}>
          {[...Array(5)].map((_, i) => (
            <div key={i} className="col-6 col-md-4 col-lg-3">
              <div className={styles.skeleton} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Repetidas;
