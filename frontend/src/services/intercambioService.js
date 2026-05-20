import { api, handleAxiosError } from "./api.js";

export const aceptarIntercambio = async (id) => {
  console.log("Mock aceptar intercambio", id);
  return true;
};

export const rechazarIntercambio = async (id) => {
  console.log("Mock rechazar intercambio", id);
  return true;
};


//Ver esto que onda si reutilizo el rechazar o hago una url aparte.
export const cancelarIntercambio = async (id) => {
  console.log("Mock cancelar intercambio", id);
  return true;
};

// ─── Datos Hardcodeados ───────────────────────────────────────────────────────
export const buscarIntercambios = async () => {
    try {
        await api.post(`/intercambios/${id}/rechazar`);
    } catch (e) {
        handleAxiosError(e);
    }

};