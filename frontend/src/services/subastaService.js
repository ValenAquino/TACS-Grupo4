import {api, handleAxiosError } from "./api.js";

const SUBASTA_URL = "/subastas";

export const buscarSubasta = async ({subId}) => {
    try {
        const { data } = await api.get(
            `${SUBASTA_URL}/${subId}`,
            {}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};