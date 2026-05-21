import { api, handleAxiosError } from "./api.js";

const PROPUESTAS_URL = "/propuestas";

export const buscarPropuestas = async (filtros) => {
    try {
        const filtrosLimpios = Object.fromEntries(
            Object.entries(filtros)
                .filter(([_, value]) => value !== "" && value != null)
        );
        const {data} = await api.get(PROPUESTAS_URL,
            {params: {...filtrosLimpios}}
        );
        return data
    } catch (error) {
        handleAxiosError(error);
    }
};

export const cancelarPropuesta = async (prop_id) => {
    try {
        const {data} = await api.patch(PROPUESTAS_URL + `/${prop_id}/cancelar`);
        return data
    } catch (error) {
        handleAxiosError(error);
    }
}

export const aceptarPropuesta = async (prop_id) => {
    try {
        const {data} = await api.patch(PROPUESTAS_URL + `/${prop_id}/aceptar`);
        return data
    } catch (error) {
        handleAxiosError(error);
    }
}

export const rechazarPropuesta = async (prop_id) => {
    try {
        const {data} = await api.patch(PROPUESTAS_URL + `/${prop_id}/rechazar`);
        return data
    } catch (error) {
        handleAxiosError(error);
    }
}