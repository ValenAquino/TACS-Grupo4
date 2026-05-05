import { api, handleAxiosError } from "./api.js";

/*
export const aceptarIntercambio = async (id) => {
  try {
    await api.post(`/intercambios/${id}/aceptar`);
  } catch (e) {
    handleAxiosError(e);
  }
};

export const rechazarIntercambio = async (id) => {
  try {
    await api.post(`/intercambios/${id}/rechazar`);
  } catch (e) {
    handleAxiosError(e);
  }
};*/

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
  return {
    pendientes: [
                    {
                        id: 1,
                        usuario: { iniciales: "RL", nombre: "rodrigo_l", estrellas: 5, intercambios: 48 },
                        pide: [{ numero: 14, nombre: "Dibu Martínez", pais: "Argentina" }],
                        ofrece: [
                            { numero: 10, nombre: "Messi Brillante", pais: "Argentina" },
                            { numero: 22, nombre: "De Bruyne", pais: "Bélgica" },
                        ],
                        ofreceMas: 0,
                        tiempo: "Hace 10 min",
                        esNueva: true,
                    },
                    {
                        id: 2,
                        usuario: { iniciales: "JM", nombre: "juani_m", estrellas: 3, intercambios: 12 },
                        pide: [{ numero: 23, nombre: "Lusail Stadium", pais: "Estadios" }],
                        ofrece: [{ numero: 7, nombre: "Vinicius Jr.", pais: "Brasil" }],
                        ofreceMas: 0,
                        tiempo: "Hace 1 h",
                        esNueva: true,
                    },
                    {
                        id: 3,
                        usuario: { iniciales: "SV", nombre: "sofi_v", estrellas: 5, intercambios: 91 },
                        pide: [{ numero: 10, nombre: "Messi", pais: "Argentina" }],
                        ofrece: [{ numero: 55, nombre: "Escudo España", pais: "España" }],
                        ofreceMas: 2,
                        tiempo: "Hace 3 h",
                        esNueva: false,
                    },
                    {
                        id: 99,
                        usuario: { iniciales: "XX", nombre: "mega_trade", estrellas: 5, intercambios: 999 },
                        pide: [
                            { numero: 1, nombre: "A", pais: "X" },
                            { numero: 2, nombre: "B", pais: "X" },
                            { numero: 3, nombre: "C", pais: "X" },
                            { numero: 4, nombre: "D", pais: "X" },
                            { numero: 5, nombre: "E", pais: "X" },
                            { numero: 6, nombre: "F", pais: "X" },
                        ],
                        ofrece: [
                            { numero: 7, nombre: "G", pais: "Y" },
                            { numero: 8, nombre: "H", pais: "Y" },
                            { numero: 9, nombre: "I", pais: "Y" },
                            { numero: 10, nombre: "J", pais: "Y" },
                            { numero: 11, nombre: "K", pais: "Y" },
                             { numero: 7, nombre: "L", pais: "Y" },
                             { numero: 8, nombre: "M", pais: "Y" },
                             { numero: 9, nombre: "N", pais: "Y" },
                             { numero: 10, nombre: "Ñ", pais: "Y" },
                             { numero: 11, nombre: "O", pais: "Y" },
                        ],
                        tiempo: "Hace 2 min",
                        esNueva: true,
                    },
                ],
    enviadas: [
                  {
                      id: 1,
                      usuario: { iniciales: "CR", nombre: "caro_r", estrellas: 5, intercambios: 67 },
                      pedis: [{ numero: 88, nombre: "Mbappé Brillante", pais: "Francia" }],
                      ofreces: [
                          { numero: 14, nombre: "Dibu Martínez", pais: "Argentina" },
                          { numero: 8, nombre: "Mac Allister", pais: "Argentina" },
                      ],
                      tiempo: "Enviada hace 30 min · Vista por el usuario",
                  },
                  {
                      id: 2,
                      usuario: { iniciales: "PM", nombre: "pedro_m", estrellas: 4, intercambios: 18 },
                      pedis: [{ numero: 31, nombre: "Wembley Stadium", pais: "Estadios" }],
                      ofreces: [{ numero: 11, nombre: "Griezmann", pais: "Francia" }],
                      tiempo: "Enviada hace 2 h · No vista aún",
                  },
              ],
    historial: [
                   {
                       id: 1,
                       usuario: { iniciales: "RL", nombre: "rodrigo_l", estrellas: 5 },
                       recibiste: [{ numero: 10, nombre: "Messi Brillante", pais: "Argentina" }],
                       entregaste: [{ numero: 14, nombre: "Dibu Martínez", pais: "Argentina" }],
                       fecha: "20 abr 2026",
                       calificado: true,
                   },
                   {
                       id: 2,
                       usuario: { iniciales: "LF", nombre: "lu_figueiras", estrellas: 3 },
                       recibiste: [{ numero: 9, nombre: "Álvarez", pais: "Argentina" }],
                       entregaste: [{ numero: 7, nombre: "Vinicius Jr.", pais: "Brasil" }],
                       fecha: "18 abr 2026",
                       calificado: false,
                   },
               ],       // HISTORIAL
  };
};