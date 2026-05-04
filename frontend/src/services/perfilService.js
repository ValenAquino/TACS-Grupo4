import {api, handleAxiosError } from "./api.js";

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