import {api, handleAxiosError } from "./api.js";

const USUARIOS_URL = "/usuarios"

export const crearUsuario = async ({nombre, contrasenia}) => {
    try {
        const { data } = await api.post(USUARIOS_URL, {nombre, contrasenia, rol: "USUARIO"})

        return data
    } catch (error) {
        handleAxiosError(error)
    }
}