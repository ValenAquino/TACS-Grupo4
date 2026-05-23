import {api, handleAxiosError } from "./api.js";

export const crearUsuario = async ({nombre, contrasenia}) => {
    try {
        const { data } = await api.post("/usuarios", {nombre, contrasenia, rol: "USUARIO"})

        return data
    } catch (error) {
        handleAxiosError(error)
    }
}

export const crearAdministrador = async ({nombre, contrasenia, rol}) => {
  try {
    const { data } = await api.post("/administradores", {nombre, contrasenia, rol: rol ?? "USUARIO"})

    return data
  } catch (error) {
    handleAxiosError(error)
  }
}