import { useState, useEffect, useRef } from "react";
import styles from "./scroll-repetidas.module.css";
import FilaScroll from "./fila-scroll/fila-scroll";
import CabeceraSeleccion from "./cabecera-seleccion/cabecera-seleccion";

const Skeleton = () => (
  <div>
    {[...Array(4)].map((_, i) => (
      <div key={i} className={styles['scroll-skeleton-fila'] + " placeholder-glow"}>
        <div className="placeholder rounded w-75" />
      </div>
    ))}
  </div>
);

const SinResultados = () => (
  <div className={styles['scroll-sin-resultados']}>No hay resultados</div>
);

const ScrollRepetidas = ({
  figuritas = [],
  loading = false,
  totalDisponibles = null,
  onBuscar,
  debounceMs = 400,
  modo = "unica",
  seleccionadas = [],
  onToggle,
  bloqueadas = [],
}) => {
  const [busqueda, setBusqueda] = useState("");
  const debounceRef = useRef(null);

  useEffect(() => {
    if (!onBuscar) return;
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => onBuscar(busqueda.trim()), debounceMs);
    return () => clearTimeout(debounceRef.current);
  }, [busqueda, onBuscar, debounceMs]);

  const filtradas = onBuscar
    ? figuritas
    : figuritas.filter(
        (f) =>
          f.jugador.toLowerCase().includes(busqueda.toLowerCase()) ||
          f.numero?.toString().includes(busqueda),
      );

  const esBloqueada      = (fig) => bloqueadas.some((b) => b.figuritaId === fig.figuritaId);
  const estaSeleccionada = (fig) => esBloqueada(fig) || seleccionadas.some((f) => f.figuritaId === fig.figuritaId);
  const mostrandoParcial = totalDisponibles !== null && totalDisponibles > filtradas.length;

  return (
    <div className="d-flex flex-column gap-3">
      <div className={styles['scroll-repetidas-buscador']}>
        <span className={styles['scroll-repetidas-buscador-icono']}>🔍</span>
        <input
          type="text"
          className="form-control ps-5"
          placeholder="Buscar en tus repetidas..."
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
        />
      </div>

      <div className={styles['scroll-repetidas-lista']}>
        <div className={styles['scroll-repetidas-header']}>
          <span className={styles['scroll-repetidas-header-titulo']}>
            Tus repetidas
            {mostrandoParcial && (
              <span className={styles['scroll-repetidas-header-parcial']}>
                · {filtradas.length} de {totalDisponibles}
              </span>
            )}
          </span>
          <CabeceraSeleccion modo={modo} seleccionadas={seleccionadas} bloqueadas={bloqueadas} />
        </div>

        <div className={styles['scroll-repetidas-scroll']}>
          {loading ? (
            <Skeleton />
          ) : filtradas.length === 0 ? (
            <SinResultados />
          ) : (
            filtradas.map((fig) => (
              <FilaScroll
                key={fig.figuritaId}
                fig={fig}
                modo={modo}
                bloqueada={esBloqueada(fig)}
                seleccionada={estaSeleccionada(fig)}
                onToggle={onToggle}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default ScrollRepetidas;