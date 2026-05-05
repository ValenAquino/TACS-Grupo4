import { api, handleAxiosError } from "./api.js";

const PERFIL_URL = "/perfil";

/*export const buscarContadores = async ({userId}) => {
    try {
        const { data } = await api.get(
            `${PERFIL_URL}/${userId}/contadores`,
            {}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }

};*/

export const buscarSugerencias = async ({userId, tipo, pagina, limite}) => {
    try {
        const { data } = await api.get(
            `${PERFIL_URL}/${userId}/sugerencias`,
            {params: {tipo, paginaActual: pagina, limite}}
        );
        return data;
    } catch (error) {
        handleAxiosError(error);
    }
};

export const calificarPerfil = async (
  autorId,
  perfilId,
  { valor, descripcion, transactionId, tipoTransaccion },
) => {
  await api.post(
    `/perfil/${perfilId}/calificaciones`,
    {
      valor,
      descripcion,
      transaction_id: transactionId,
      tipo_transaccion: tipoTransaccion,
    },
    { headers: { autor_id: autorId } },
  );
};

// export const buscarFaltantes = async (userId) => {
//   try {
//     const { data } = await api.get(`${PERFIL_URL}/${userId}/faltantes`);
//     return data;
//   } catch (error) {
//     handleAxiosError(error);
//   }
// };

// export const buscarRepetidas = async (userId) => {
//   try {
//     const { data } = await api.get(`${PERFIL_URL}/${userId}/repetidas`);
//     return data;
//   } catch (error) {
//     handleAxiosError(error);
//   }
// };
////////////////////////////////////////////////////
export const buscarFaltantes = async (userId) => {
  return [
    { id: "f1", jugador: "Neymar Brillante", seleccion: "BRASIL", numero: 45 },
    { id: "f2", jugador: "De Bruyne", seleccion: "BELGICA", numero: 22 },
    { id: "f3", jugador: "Benzema Dorado", seleccion: "FRANCIA", numero: 9 },
    { id: "f4", jugador: "Pedri", seleccion: "ESPAÑA", numero: 8 },
    { id: "f5", jugador: "Salah", seleccion: "EGIPTO", numero: 11 },
    { id: "f6", jugador: "Haaland", seleccion: "NORUEGA", numero: 9 },
    { id: "f7", jugador: "Musiala", seleccion: "ALEMANIA", numero: 14 },
    { id: "f8", jugador: "Bellingham", seleccion: "INGLATERRA", numero: 10 },
  ];
};

export const buscarRepetidas = async (userId) => {
  return [
    {
      figurita_id: "r1",
      jugador: "Mbappé Brillante",
      seleccion: "FRANCIA",
      numero: 88,
      cantidadExistente: 1,
    },
    {
      figurita_id: "r2",
      jugador: "Messi Brillante",
      seleccion: "ARGENTINA",
      numero: 10,
      cantidadExistente: 2,
    },
    {
      figurita_id: "r3",
      jugador: "Vinicius Jr.",
      seleccion: "BRASIL",
      numero: 7,
      cantidadExistente: 3,
    },
    {
      figurita_id: "r4",
      jugador: "Lusail Stadium",
      seleccion: "ESTADIOS",
      numero: 23,
      cantidadExistente: 1,
    },
    {
      figurita_id: "r5",
      jugador: "Mac Allister",
      seleccion: "ARGENTINA",
      numero: 6,
      cantidadExistente: 2,
    },
  ];
};

export const buscarPerfil = async (userId) => {
  // Mockeado por ahora.
  return {
    nombre: "Messi G.",
    username: "messi_g",
    iniciales: "MG",
    promedio: 4.6,
    totalCalificaciones: 4
  };
};

export const buscarCalificaciones = async (userId) => {
  return [
    {
      nombre: "carlos_r",
      puntaje: 9,
      comentario: "Muy buena operación, rápido y confiable.",
      iniciales: "CR"
    },
    {
      nombre: "juan123",
      puntaje: 8,
      comentario: "Todo ok.",
      iniciales: "JU"
    },
    {
      nombre: "Roberto_Carlos",
      puntaje: 2,
      comentario: "La figurita venia pegoteada.",
      iniciales: "RC"
    },
    {
      nombre: "Buscando_Bugs",
      puntaje: 10,
      comentario: "Susanita tiene un raton, un raton chiquitin.__________________________________________________________________________________________________",
      iniciales: "BB"
    }
  ];
};

export const buscarContadores = async ({ userId }) => {
  return {
    intercambios: 24,
    publicadas: 5,
    faltantes: 12,
    subastas: 3
  };
};