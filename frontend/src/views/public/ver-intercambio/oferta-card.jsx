import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import FiguritaCard from "./figurita-card.jsx";
import styles from "./oferta-card.module.css";
import { mostrar_label } from "../../../utils/estandarizar.jsx";

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
            <div
                className={
                    styles.ofertaCard +
                    " ps-3 pe-3 pt-1 pb-1 d-flex flex-row align-items-center gap-3"
                }
            >
                {
                    position ? (
                        <div
                            className={
                                styles.position +
                                " p-3 d-flex align-items-center justify-content-center"
                            }
                        >
                            {position}°
                        </div>
                    ) : null
                }

                <div>
                    <PerfilSimple perfil={propuesta.autor} />

                    {
                        mostrar_label(propuesta)
                    }
                </div>
            </div>
        );
    }

    // =========================
    // Nuevo modo intercambio
    // =========================

    const esRecibida = tipo === "RECIBIDA";

    const usuarioIzquierda =
        esRecibida
            ? propuesta.autor
            : propuesta.destinatario;

    const usuarioDerecha =
        esRecibida
            ? propuesta.destinatario
            : propuesta.autor;

    return (
        <div className={styles.ofertaCard + " p-3"}>

            <div className="row align-items-center">

                {/* IZQUIERDA */}
                <div className="col">

                    <div className="mb-3">
                        <PerfilSimple perfil={usuarioIzquierda} />
                    </div>

                    <div className="d-flex flex-column gap-2">

                        {
                            propuesta?.figurita_buscada && (
                                <FiguritaCard
                                    figurita={propuesta.figurita_buscada}
                                />
                            )
                        }

                    </div>

                </div>

                {/* FLECHAS */}
                <div className="col-auto text-center">

                    <div className="fs-4 fw-bold">
                        →
                    </div>

                    <div className="fs-4 fw-bold">
                        ←
                    </div>

                </div>

                {/* DERECHA */}
                <div className="col">

                    <div className="mb-3">
                        <PerfilSimple perfil={usuarioDerecha} />
                    </div>

                    <div className="d-flex flex-column gap-2">

                        {
                            propuesta?.figuritas_ofrecidas?.map((fig) => (
                                <FiguritaCard
                                    key={fig.id}
                                    figurita={fig}
                                />
                            ))
                        }

                    </div>

                </div>

            </div>

        </div>
    );
};

export default OfertaCard;