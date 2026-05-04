import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";
import RecibidasTab from "./tabs/RecibidasTab.jsx";
import EnviadasTab from "./tabs/EnviadasTab.jsx";
import HistorialTab from "./tabs/HistorialTab.jsx";

// ─── Datos Hardcodeados ───────────────────────────────────────────────────────

const PENDIENTES = [
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
];

const RESUELTAS_RECIBIDAS = [
    {
        id: 4,
        usuario: { iniciales: "PF", nombre: "pedro_f", estrellas: 4, intercambios: 29 },
        pedida: [{ numero: 14, nombre: "Dibu Martínez", pais: "Argentina" }],
        ofrecida: [{ numero: 9, nombre: "Benzema", pais: "Francia" }],
        tiempo: "Hace 5 h",
        estado: "Rechazada",
        colorEstado: "danger",
        nota: "Rechazaste esta propuesta",
    },
];

const ESPERANDO_ENVIADAS = [
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
];

const RESUELTAS_ENVIADAS = [
    {
        id: 3,
        usuario: { iniciales: "LF", nombre: "lu_figueiras", estrellas: 3, intercambios: 8 },
        pediste: [{ numero: 9, nombre: "Álvarez", pais: "Argentina" }],
        ofreciste: [{ numero: 7, nombre: "Vinicius Jr.", pais: "Brasil" }],
        tiempo: "Hace 1 día · Intercambio concretado",
        estado: "Aceptada",
        colorEstado: "success",
        puedeCalificar: true,
    },
    {
        id: 4,
        usuario: { iniciales: "MG", nombre: "mari_g", estrellas: 4, intercambios: 22 },
        pediste: [{ numero: 10, nombre: "Messi Brillante", pais: "Argentina" }],
        ofreciste: [{ numero: 14, nombre: "Dibu Martínez", pais: "Argentina" }],
        tiempo: "Hace 3 días · El usuario rechazó tu propuesta",
        estado: "Rechazada",
        colorEstado: "danger",
        puedeCalificar: false,
    },
];

const HISTORIAL = [
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
];

// ─── Página ───────────────────────────────────────────────────────────────────

const Intercambios = () => {
    const tabs = [
        {
            key: "recibidas",
            label: `Recibidas (${PENDIENTES.length})`,
            component: RecibidasTab,
            props: { pendientes: PENDIENTES, resueltas: RESUELTAS_RECIBIDAS },
        },
        {
            key: "enviadas",
            label: "Enviadas",
            component: EnviadasTab,
            props: { esperando: ESPERANDO_ENVIADAS, resueltas: RESUELTAS_ENVIADAS },
        },
        {
            key: "historial",
            label: "Historial",
            component: HistorialTab,
            props: { intercambios: HISTORIAL },
        },
    ];

    return (
        <div className="container py-3" style={{ maxWidth: 900 }}>

        <style>
            {`
            .tabs-fullwidth .nav {
              display: flex;
              width: 100%;
              background-color: #e9f5ee;
              border-radius: 8px;
              overflow: hidden;
            }

            .tabs-fullwidth .nav-item {
              flex: 1;
            }

            .tabs-fullwidth .nav-link {
              width: 100%;
              text-align: center;
              color: #175A2D;
              font-weight: 600;
              border-radius: 0;
              transition: all 0.2s ease;
            }

            /* TAB ACTIVO */
            .tabs-fullwidth .nav-link.active {
              background-color: #175A2D;
              color: white;
              border-color: #175A2D;
            }

            /* HOVER */
            .tabs-fullwidth .nav-link:hover {
              background-color: #1f6b3a;
              color: white;
            }
            `}
        </style>
            <h2
              className="fw-bold text-center mb-4"
              style={{ color: "#175A2D" }}
            >
              Intercambios
            </h2>
            <div className="tabs-fullwidth">
              <TabsContainer tabs={tabs} />
            </div>
        </div>
    );
};

export default Intercambios;