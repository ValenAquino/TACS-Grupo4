import { api, handleAxiosError } from "./api.js";

const PROPUESTAS_URL = "/propuestas";

export const buscarEnviadas = async (user_id, filtros) => {
    try {
        const {data} = await api.get(PROPUESTAS_URL,
            {params: {user_id, ...filtros}}
        );
        console.log("data", data);
        return data
    } catch (error) {
        handleAxiosError(error);
    }
};