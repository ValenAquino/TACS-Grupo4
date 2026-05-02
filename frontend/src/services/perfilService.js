import {api, handleAxiosError } from "./api.js";

const PERFIL_URL = "/perfil";

export const buscarStatsSimples = async () => {
    try {
        const { data } = await api.get(
            `${PERFIL_URL}/statsSimples`,
            {}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const buscarSugerencias = async ({userId}) => {
    try {
        const { data } = await api.get(
            `${PERFIL_URL}/${userId}/sugerencias`
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};