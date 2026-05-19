import { api, handleAxiosError } from "./api.js";

const PERFIL_URL = "/perfil";

export const buscarContadoresSugerencias = async ({userId}) => {
    try {
        const { data } = await api.get(
            `${PERFIL_URL}/${userId}/contadores`,
            {}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }

};

export const buscarSugerencias = async ({userId, tipo, pagina, limite}) => {
    try {
        const { data } = await api.get(
            `${PERFIL_URL}/${userId}/sugerencias`,
            {params: {tipo, paginaActual: pagina, limite}}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const calificarPerfil = async (
  autorId,
  perfilId,
  { valor, descripcion, transactionId, tipoTransaccion },
) => {
  await api.post(
    `/perfil/${perfilId}/calificaciones`,
    {
        user_id: autorId,
      valor,
      descripcion,
      transaction_id: transactionId,
      tipo_transaccion: tipoTransaccion,
    }
  );
};

export const buscarPerfil = async (perfilId) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/${perfilId}`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

//TODO: Eliminar esta funcion. Si se quieren las faltantes de un perfil, se tiene que usar la coleccion.
export const buscarFaltantes = async (userId) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/${userId}/faltantes`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

//TODO: Eliminar esta funcion. Si se quieren las repetidas de un perfil, se tiene que usar la coleccion.
export const buscarRepetidas = async (userId) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/${userId}/repetidas`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarCalificaciones = async (perfilId, filtros) => {
    try {
        const { data } = await api.get(`${PERFIL_URL}/${perfilId}/calificaciones`,
            {params: filtros}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const buscarContadores = async (perfilId) => {
    try {
        const { data } = await api.get(`${PERFIL_URL}/${perfilId}/contadores`);
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};