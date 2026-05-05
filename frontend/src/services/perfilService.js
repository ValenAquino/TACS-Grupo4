import { api, handleAxiosError } from "./api.js";

const PERFIL_URL = "/perfil";

export const buscarContadores = async ({userId}) => {
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
  autorId, perfilId,
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

export const buscarPerfil = async (userId) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/${userId}`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarFaltantes = async (userId) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/${userId}/faltantes`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarRepetidas = async (userId) => {
  try {
    const { data } = await api.get(`${PERFIL_URL}/${userId}/repetidas`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};
