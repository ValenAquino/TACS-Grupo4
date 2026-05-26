import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import FiguritaCard from "./figurita-card.jsx";
import styles from "./oferta-card.module.css";

const mostrarLabel = (propuesta) => {
    return propuesta.figuritas_ofrecidas.map((fig, index) => (
        <span key={index} className={styles.figuritaOfrecida}>
            {index > 0 && " + "}
            {fig.jugador} #{fig.numero}
        </span>
    ));
};

const OfertaCard = ({
    propuesta,
    position = undefined,
    tipo = "RECIBIDA"
}) => {

    // =========================
    // Compatibilidad vieja
    // =========================
    if (!propuesta?.figurita_buscada && !propuesta?.figuritas_ofrecidas) {
        return (
            <div className={styles.ofertaCard + " ps-3 pe-3 pt-1 pb-1 d-flex flex-row align-items-center gap-3"}>
                {position ? (
                    <div className={styles.position + " p-3 d-flex align-items-center justify-content-center"}>
                        {position}°
                    </div>
                ) : null}
                <div>
                    <PerfilSimple perfil={propuesta.autor} />
                    {mostrarLabel(propuesta)}
                </div>
            </div>
        );
    }

    // =========================
    // Nuevo modo intercambio
    // =========================
    const esRecibida = tipo === "RECIBIDA";

    const usuarioIzquierda = esRecibida ? propuesta.autor : propuesta.autor;
    const usuarioDerecha = esRecibida ? propuesta.destinatario : propuesta.destinatario;

    const figuritasIzquierda = esRecibida
        ? propuesta.figuritas_ofrecidas || []   // lo que el otro ofrece (yo recibo)
        : propuesta.figuritas_ofrecidas || [];  // lo que yo ofrezco

    const figuritasDerecha = esRecibida
        ? [propuesta.figurita_buscada]          // lo que el otro quiere (yo doy)
        : [propuesta.figurita_buscada];         // lo que yo quiero

    const tituloIzquierda = esRecibida ? "Vos recibís" : "Vos ofrecés";
    const tituloDerecha = esRecibida ? "Vos entregás" : "Vos recibís";

    return (
        <div className={styles.ofertaCard + " p-3"}>
            <div className="row align-items-center">

                {/* IZQUIERDA */}
                <div className="col">
                    <div className="mb-1">
                        <PerfilSimple perfil={usuarioIzquierda} />
                    </div>
                    <small className="text-uppercase text-muted fw-semibold d-block mb-2">
                        {tituloIzquierda}
                    </small>
                    <div className="d-flex flex-column gap-2">
                        {figuritasIzquierda.filter(Boolean).map((fig, i) => (
                            <FiguritaCard key={fig.id || i} figurita={fig} />
                        ))}
                    </div>
                </div>

                {/* FLECHAS */}
                <div className="col-auto text-center">
                    <div className="fs-4 fw-bold">→</div>
                    <div className="fs-4 fw-bold">←</div>
                </div>

                {/* DERECHA */}
                <div className="col">
                    <div className="mb-1">
                        <PerfilSimple perfil={usuarioDerecha} />
                    </div>
                    <small className="text-uppercase text-muted fw-semibold d-block mb-2">
                        {tituloDerecha}
                    </small>
                    <div className="d-flex flex-column gap-2">
                        {figuritasDerecha.filter(Boolean).map((fig, i) => (
                            <FiguritaCard key={fig.id || i} figurita={fig} />
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
};

export default OfertaCard;