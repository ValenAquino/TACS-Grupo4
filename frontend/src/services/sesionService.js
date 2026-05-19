import {api, handleAxiosError } from "./api.js";

export const iniciarSesion = async ({nombre, contrasenia}) => {
    try {
        const { data } = await api.post("/login", {nombre, contrasenia})
        return data
    } catch (error) {
        handleAxiosError(error)
    }
}

export const buscarUsuario = async (asignarUsuario) => {
    try {
        const { data } = await api.get("/yo")
        asignarUsuario(data)
        return data
    } catch (error) {
        handleAxiosError(error)
    }
}

export const logout = async () => {
    try {
        const { data } = await api.delete("/sesion")

        return data
    } catch (error) {
        handleAxiosError(error)
    }
}

export const registrarUsuario = async ({nombre, contrasenia}) => {
    try {
        const { data } = await api.post("/registrar", {nombre, contrasenia, rol: "USUARIO"})

        return data
    } catch (error) {
        handleAxiosError(error)
    }
}