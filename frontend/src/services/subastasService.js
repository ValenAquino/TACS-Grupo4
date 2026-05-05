import { api, handleAxiosError } from "./api.js";

const SUBASTAS_URL = "/subastas";

export const crearSubasta = async (userId, body) => {
  try {
    await api.post("/subastas", body, {
      headers: { user_id: userId },
    });
  } catch (error) {
    handleAxiosError(error);
  }
};

export const buscarSubasta = async ({subId}) => {
    try {
        const { data } = await api.get(
            `${SUBASTAS_URL}/${subId}`,
            {}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const buscarMisSubastas = async (userId, filtros) => {
    try {
        const { data } = await api.get(
            `${SUBASTAS_URL}/mis-subastas`,
            { params: {
                ...filtros,
                    userId
                }
            }
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

// export const buscarSubastasParticipo = async (filtros) => {
//     try {
//         const { data } = await api.get(
//             `${SUBASTAS_URL}/participo`,
//             { params: filtros }
//         );
//         return data;
//     } catch (error) {
//         handleAxiosError(error);
//     }
// };

export const seleccionarOferta = async (subastaId, ofertaId) => {
  try {
    const { data } = await api.post(
      `${SUBASTAS_URL}/${subastaId}/ofertas/${ofertaId}/seleccionar`,
    );
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const rechazarOferta = async (subastaId, ofertaId) => {
  try {
    const { data } = await api.post(
      `${SUBASTAS_URL}/${subastaId}/ofertas/${ofertaId}/rechazar`,
    );
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const cancelarSubasta = async (subastaId) => {
  try {
    const { data } = await api.post(`${SUBASTAS_URL}/${subastaId}/cancelar`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};

export const cerrarSubasta = async (subastaId) => {
  try {
    const { data } = await api.post(`${SUBASTAS_URL}/${subastaId}/cerrar`);
    return data;
  } catch (error) {
    handleAxiosError(error);
  }
};
//////////////////
const ahora = new Date();
const en2h = new Date(ahora.getTime() + 2 * 60 * 60 * 1000).toISOString();
const en1dia = new Date(ahora.getTime() + 1 * 24 * 60 * 60 * 1000).toISOString();
const hace5dias = new Date(ahora.getTime() - 5 * 24 * 60 * 60 * 1000).toISOString();

export const buscarSubastasParticipo = async (_filtros) => {
  return {
    activas: [
      {
        id: "4",
        autor: { nombre: "pedro_f", perfilId: "p1" },
        figuritaSubastada: {
          jugador: "Vinicius Jr.",
          seleccion: { nombre: "Brasil" },
          numero: 7,
        },
        fechaInicio: ahora.toISOString(),
        fechaCierre: en2h,
        tuOferta: {
          label: "Coman #11 + Griezmann Dorado",
          estado: "SELECCIONADO",
        },
        ya_calificado: false,
      },
      {
        id: "5",
        autor: { nombre: "sofi_v", perfilId: "p2" },
        figuritaSubastada: {
          jugador: "Messi Brillante",
          seleccion: { nombre: "Argentina" },
          numero: 10,
        },
        fechaInicio: ahora.toISOString(),
        fechaCierre: en1dia,
        tuOferta: {
          label: "Di María #11 + Dybala Especial",
          estado: "RECHAZADO",
        },
        ya_calificado: false,
      },
    ],
    finalizadas: [
      {
        id: "6",
        autor: { nombre: "juani_m", perfilId: "p3" },
        figuritaSubastada: {
          jugador: "Neymar Brillante",
          seleccion: { nombre: "Brasil" },
          numero: 45,
        },
        fechaInicio: hace5dias,
        fechaCierre: hace5dias,
        tuOferta: { label: "Mbappé #7 + Giroud #9", estado: "ACEPTADO" },
        ya_calificado: false,
      },
      {
        id: "8",
        autor: { nombre: "sofi_v", perfilId: "p2" },
        figuritaSubastada: {
          jugador: "Griezmann",
          seleccion: { nombre: "Francia" },
          numero: 11,
        },
        fechaInicio: hace5dias,
        fechaCierre: hace5dias,
        tuOferta: { label: "Benzema Dorado #9", estado: "ACEPTADO" },
        ya_calificado: true,
      },
    ],
  };
};