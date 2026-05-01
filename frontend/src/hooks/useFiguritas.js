import { useEffect, useState } from 'react';
import { explorarFiguritas, MOCK_FIGURITAS } from '@/services/explorarService';

const useFiguritas = (jugador, seleccion, numero, tipo) => {
  const [figuritas, setFiguritas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true);
        setError(false);
        const data = await explorarFiguritas({ jugador, seleccion, numero, tipo });
        setFiguritas(data);
      } catch {
        // TODO: eliminar fallback cuando se integre el backend
        setFiguritas(MOCK_FIGURITAS);
      } finally {
        setLoading(false);
      }
    };

    const debounce = setTimeout(cargar, 300);
    return () => clearTimeout(debounce);
  }, [jugador, seleccion, numero, tipo]);

  return { figuritas, loading, error };
};

export default useFiguritas;
