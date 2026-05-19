import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URI || 'http://localhost:8080',
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true
});

export const ping = async () => {
    try {
        await api.get("/ping")
    } catch (error) {
        handleAxiosError(error);
    }
};

const handleAxiosError = (error) => {

    if (error.code === "ERR_NETWORK") {
        throw {
            type: "SERVER_DOWN",
            message: "Servidor no disponible",
        };
    }

    if (error.response) {
        const { status, data } = error.response;

        switch (status) { //Errores que deben quitarte de la pagina
            case 401:
                throw { type: "UNAUTHORIZED", message: "No se encontro una sesión" };

            case 403:
                throw {type: "FORBIDDEN", message: "Accion no permitida"}

            case 500:
                throw {
                    type: "INTERNAL_SERVER_ERROR",
                    message: "Ocurrió un error interno"
                };
        }

        throw { //Errores que deben mostrar el mensaje en la misma pagina
            type: "API_ERROR",
            status,
            message: data.message || "Error del servidor",
            errors: data.errors || {}
        };
    }

    if (error.request) {
        throw {
            type: "SERVER_DOWN",
            message: "Servidor no disponible",
        };
    }

    throw {
        type: "UNKNOWN",
        message: "Error inesperado",
    };
};


export {api, handleAxiosError};