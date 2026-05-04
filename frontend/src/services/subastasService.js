import { api, handleAxiosError } from "./api.js";

const SUBASTAS_URL = "/subastas";

// export const buscarMisSubastas = async (filtros) => {
//     try {
//         const { data } = await api.get(
//             `${SUBASTAS_URL}/mis-subastas`,
//             { params: filtros }
//         );
//         return data;
//     } catch (error) {
//         handleAxiosError(error);
//     }
// };

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

export const buscarMisSubastas = async (_filtros) => {
  return {
    activas: [
      {
        id: "1",
        figuritaSubastada: {
          jugador: "Mbappé Brillante",
          seleccion: { nombre: "Francia" },
          numero: 88,
        },
        tiempoRestante: "45 min",
        finalizada: false,
        finalizaPronto: true,
        cantidadOfertas: 3,
        ofertas: [
          {
            id: "o1",
            usuario: "caro_r",
            iniciales: "CR",
            calificacion: 4.8,
            label: "Neymar Brillante #45 + Vinicius #7",
            seleccionada: true,
          },
          {
            id: "o2",
            usuario: "pedro_m",
            iniciales: "PM",
            calificacion: 4.0,
            label: "De Bruyne #10 + Benzema Dorado",
            seleccionada: false,
          },
          {
            id: "o3",
            usuario: "lu_figueiras",
            iniciales: "LU",
            calificacion: 3.2,
            label: "Griezmann #11 + Giroud #9",
            seleccionada: false,
          },
        ],
        ganador: null,
      },
      {
        id: "2",
        figuritaSubastada: {
          jugador: "Lusail Stadium",
          seleccion: { nombre: "Estadios" },
          numero: 23,
        },
        tiempoRestante: "2 días 14 h",
        finalizada: false,
        finalizaPronto: false,
        cantidadOfertas: 0,
        ofertas: [],
        ganador: null,
      },
    ],
    finalizadas: [
      {
        id: "3",
        figuritaSubastada: {
          jugador: "Dibu Martínez",
          seleccion: { nombre: "Argentina" },
          numero: 14,
        },
        finalizadaHace: "2 días",
        finalizada: true,
        finalizaPronto: false,
        cantidadOfertas: 5,
        ofertas: [],
        ganador: {
          perfilId: "123",
          usuario: "rodrigo_l",
          label: "Messi Brillante + Álvarez #9",
        },
      },
    ],
    paginas_totales: 1,
  };
};

export const buscarSubastasParticipo = async (_filtros) => {
  return {
    activas_count: 2,
    seleccionadas_count: 1,
    resultados: 3,
    paginas_totales: 1,
    data: [
      {
        id: "4",
        autor: { nombre: "pedro_f" },
        figuritaSubastada: {
          jugador: "Vinicius Jr.",
          seleccion: { nombre: "Brasil" },
          numero: 7,
        },
        tiempoRestante: "2 h 14 min",
        finalizada: false,
        finalizaPronto: true,
        tuOferta: {
          label: "Coman #11 + Griezmann Dorado",
          estado: "SELECCIONADO",
        },
      },
      {
        id: "5",
        autor: { nombre: "sofi_v" },
        figuritaSubastada: {
          jugador: "Messi Brillante",
          seleccion: { nombre: "Argentina" },
          numero: 10,
        },
        tiempoRestante: "1 día 8 h",
        finalizada: false,
        finalizaPronto: false,
        tuOferta: {
          label: "Di María #11 + Dybala Especial",
          estado: "RECHAZADO",
        },
      },
      {
        id: "6",
        autor: { nombre: "juani_m" },
        figuritaSubastada: {
          jugador: "Neymar Brillante",
          seleccion: { nombre: "Brasil" },
          numero: 45,
        },
        finalizadaHace: "5 días",
        finalizada: true,
        finalizaPronto: false,
        tuOferta: { label: "Mbappé #7 + Giroud #9", estado: "ACEPTADO" },
      },
    ],
  };
};
