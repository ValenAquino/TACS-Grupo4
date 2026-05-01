import {api, handleAxiosError } from "./api.js";

const FIGURITAS_URL = "/figuritas";

export const buscarFiguritas = async (filtros) => {
    try {
        const { data } = await api.get(
            `${FIGURITAS_URL}`,
            {params: filtros}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};









