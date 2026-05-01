import {api, handleAxiosError } from "./api.js";

const COLECCIONES_URL = "/colecciones";

export const buscarFaltantes = async (colId, filtros) => {
    try {
        const { data } = await api.get(
            `${COLECCIONES_URL}/${colId}/faltantes`,
            {params: filtros}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const buscarRepetidas = async (colId, filtros) => {
    try {
        const { data } = await api.get(
            `${COLECCIONES_URL}/${colId}/repetidas`,
            {params: filtros},
        );

        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const agregarFaltante = async (colId, faltante) => {
    try {
        const { data } = await api.post(
            `${COLECCIONES_URL}/${colId}/faltantes`,
            {fig_id: faltante.id}
        );

        return data;
    } catch (error) {
        handleAxiosError(error);
    }
}