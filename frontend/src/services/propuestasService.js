import { api, handleAxiosError } from "./api.js";

const PROPUESTAS_URL = "/propuestas";

export const buscarPropuestas = async (user_id, filtros) => {
    try {

        const filtrosLimpios = Object.fromEntries(
            Object.entries(filtros)
                .filter(([_, value]) => value !== "" && value != null)
        );

        const {data} = await api.get(PROPUESTAS_URL,
            {params: {user_id, ...filtrosLimpios}}
        );

        return data
    } catch (error) {
        handleAxiosError(error);
    }
};