import Button from "@/components/ui/button/button.jsx";
import SectionCard from "@/components/ui/section-card/section-card.jsx";
import HeaderUsuarioEstado from "@/components/ui/intercambio-card/header-usuario-estado.jsx";
import {useNavigate} from "react-router";
import {cancelarPropuesta, aceptarPropuesta, rechazarPropuesta} from "@/services/propuestasService.js";
import {calificarPerfil} from "@/services/perfilService.js";
import CalificarModal from "@/components/ui/calificar-modal/calificar-modal.jsx";
import {useState} from "react";
import ConfirmModal from "@/components/ui/confirm-modal/confirm-modal.jsx";

const ChipFigurita = ({ figurita }) => (
    <div className="border rounded p-2 mb-1 d-flex align-items-center gap-2">
        <small className="text-muted">#{figurita.id}</small>
        <span>{figurita.jugador}</span>
        {figurita.seleccion && <small className="text-muted">· {figurita.seleccion}</small>}
    </div>
);


const IntercambioCard = ({ intercambio, tipo = "RECIBIDA" }) => {

    const [showCalificacion, setShowCalificacion] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);

    const izq = [intercambio.figurita_buscada] || [];
    const der = intercambio.figuritas_ofrecidas || [];

    const estado = intercambio.estado;

    const navigate = useNavigate();

    const usuarioRelacionado =
        tipo === "RECIBIDA"
            ? intercambio.autor
            : intercambio.destinatario;

    const autorCalificacion =
        tipo === "RECIBIDA"
            ? intercambio.destinatario.id
            : intercambio.autor.id;

    const perfilCalificado =
        tipo === "RECIBIDA"
            ? intercambio.autor.id
            : intercambio.destinatario.id;

    const esRecibida = tipo === "RECIBIDA";
    const esEnviada = tipo === "ENVIADA";

    const puedeCancelar =
        esEnviada && estado === "PENDIENTE";

    const puedeRechazar =
        esRecibida && estado === "PENDIENTE";

    const puedeAceptar =
        esRecibida && estado === "PENDIENTE";

    const puedeCalificar =
        estado === "ACEPTADO";

    const puedeVerDetalle = !(puedeAceptar || puedeRechazar)

    const ejecutarCancelar = async () => {
        try {
            await cancelarPropuesta(intercambio.id);
            setConfirmAction(null);
        } catch (e) {
            console.error(e);
        }
    };

    const ejecutarAceptar = async () => {
        try {
            await aceptarPropuesta(intercambio.id);
            setConfirmAction(null);
        } catch (e) {
            console.error(e);
        }
    };

    const ejecutarRechazar = async () => {
        try {
            await rechazarPropuesta(intercambio.id);
            setConfirmAction(null);
        } catch (e) {
            console.error(e);
        }
    };

    const confirmConfig = {
        CANCELAR: {
            titulo: "Cancelar propuesta",
            mensaje: "¿Seguro que querés cancelar esta propuesta?",
            labelConfirmar: "Sí, cancelar",
            onConfirmar: ejecutarCancelar,
        },

        ACEPTAR: {
            titulo: "Aceptar intercambio",
            mensaje: "¿Querés aceptar este intercambio?",
            labelConfirmar: "Aceptar",
            onConfirmar: ejecutarAceptar,
        },

        RECHAZAR: {
            titulo: "Rechazar intercambio",
            mensaje: "¿Seguro que querés rechazar esta propuesta?",
            labelConfirmar: "Rechazar",
            onConfirmar: ejecutarRechazar,
        },
    };

    const handleCalificar = async ({ valor, descripcion }) => {
        try {
            await calificarPerfil(
                autorCalificacion,
                perfilCalificado,
                {
                    valor,
                    descripcion,
                    transactionId: intercambio.id,
                    tipoTransaccion: "INTERCAMBIO",
                }
            );
            setShowCalificacion(false);

        } catch (e) {
            console.error(e);
        }
    };

    return (
        <>
            <SectionCard>
                <SectionCard.Section>
                    <HeaderUsuarioEstado
                        estado={estado}
                        destinatario={usuarioRelacionado}
                    />
                </SectionCard.Section>

                <SectionCard.Section>
                    <div className="row mt-2">
                        <div className="col">
                            <small className="text-uppercase text-muted fw-semibold">
                                Vos pedís
                            </small>

                            {izq.map((f) => (
                                <ChipFigurita key={f.id} figurita={f} />
                            ))}
                        </div>

                        <div className="col-auto d-flex align-items-center">
                            ⇄
                        </div>

                        <div className="col">
                            <small className="text-uppercase text-muted fw-semibold">
                                Vos ofrecés
                            </small>

                            {der.map((f) => (
                                <ChipFigurita key={f.id} figurita={f} />
                            ))}
                        </div>
                    </div>
                </SectionCard.Section>

                <SectionCard.Section>
                    <div className="d-flex gap-2 mt-3">

                        {(puedeVerDetalle && <Button
                            className={"w-100"}
                            label={"Ver detalle"}
                            onClick={() => navigate(`/intercambios/${intercambio.id}`)}
                        />)}

                        {puedeCancelar && (
                            <Button
                                className={"w-50"}
                                label={"Cancelar"}
                                onClick={() => {setConfirmAction("CANCELAR")}}
                            />
                        )}

                        {puedeCalificar && (
                            <Button
                                className={"w-50"}
                                label={"⭐ Calificar usuario"}
                                onClick={() => setShowCalificacion(true)}
                            />
                        )}

                        {puedeRechazar && (
                            <Button
                                className={"w-50"}
                                label={"Rechazar"}
                                onClick={() => {setConfirmAction("RECHAZAR")}}
                            />
                        )}

                        {puedeAceptar && (
                            <Button
                                className={"w-50"}
                                label={"Aceptar"}
                                onClick={() => {setConfirmAction("ACEPTAR")}}
                            />
                        )}

                    </div>
                </SectionCard.Section>
            </SectionCard>

            <CalificarModal
                show={showCalificacion}
                usuario={usuarioRelacionado.nombre}
                onCancelar={() => setShowCalificacion(false)}
                onConfirmar={handleCalificar}
            />

            <ConfirmModal
                show={!!confirmAction}
                titulo={confirmConfig[confirmAction]?.titulo}
                mensaje={confirmConfig[confirmAction]?.mensaje}
                labelConfirmar={confirmConfig[confirmAction]?.labelConfirmar}
                onConfirmar={confirmConfig[confirmAction]?.onConfirmar}
                onCancelar={() => setConfirmAction(null)}
            />
        </>
    );
};

export default IntercambioCard;