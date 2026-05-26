import { api, handleAxiosError } from "@/services/api.js";

export const obtenerNotificaciones = async () => {
    try {
        const { data } = await api.get("/perfil/notificaciones");
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const marcarTodasLeidas = async () => {
    try {
        await api.patch("/perfil/notificaciones/leidas");
    } catch (error) {
        handleAxiosError(error);
    }
};