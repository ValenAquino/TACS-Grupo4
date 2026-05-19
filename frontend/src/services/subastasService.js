import { api, handleAxiosError } from "./api.js";

const SUBASTAS_URL = "/subastas";

export const crearSubasta = async (userId, body) => {
  try {
    await api.post("/subastas", { ...body, user_id: userId });
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarSubasta = async ({ subId }) => {
  try {
    const { data } = await api.get(`${SUBASTAS_URL}/${subId}`, {});
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarMisSubastas = async (userId, filtros) => {
  try {
    const { data } = await api.get(`${SUBASTAS_URL}/mis-subastas`, {
      params: {
        ...filtros,
        userId,
      },
    });
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarSubastasParticipo = async (userId, filtros = {}) => {
  try {
    const { data } = await api.get(`${SUBASTAS_URL}/participo`, {
      params: {
        ...filtros,
        userId,
      },
    });
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const crearOferta = async (subastaId, userId, body) => {
  try {
    const { data } = await api.post(
      `${SUBASTAS_URL}/${subastaId}/ofertas`,
      { figuritas_ofrecidas_id:body, autor_id: userId }
    );
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const seleccionarOferta = async (subastaId, ofertaId) => {
  try {
    const { data } = await api.post(
      `${SUBASTAS_URL}/${subastaId}/ofertas/${ofertaId}/seleccionar`,
    );
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const rechazarOferta = async (subastaId, ofertaId) => {
  try {
    const { data } = await api.post(
      `${SUBASTAS_URL}/${subastaId}/ofertas/${ofertaId}/rechazar`,
    );
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const cancelarSubasta = async (subastaId) => {
  try {
    const { data } = await api.post(`${SUBASTAS_URL}/${subastaId}/cancelar`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const cerrarSubasta = async (subastaId) => {
  try {
    const { data } = await api.post(`${SUBASTAS_URL}/${subastaId}/cerrar`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};
