import Button from "@/components/ui/button/button.jsx";
import SectionCard from "@/components/ui/section-card/section-card.jsx";
import HeaderUsuarioEstado from "@/components/ui/intercambio-card/header-usuario-estado.jsx";
import {useNavigate} from "react-router";
import {cancelarPropuesta} from "@/services/propuestasService.js";
import {calificarPerfil} from "@/services/perfilService.js";

const ChipFigurita = ({ figurita }) => (
    <div className="border rounded p-2 mb-1 d-flex align-items-center gap-2">
        <small className="text-muted">#{figurita.id}</small>
        <span>{figurita.jugador}</span>
        {figurita.seleccion && <small className="text-muted">· {figurita.seleccion}</small>}
    </div>
);


const IntercambioCard = ({
    intercambio
}) => {

    const izq = [intercambio.figurita_buscada] || [];
    const der = intercambio.figuritas_ofrecidas || [];
    const estado = intercambio.estado;
    const navigate = useNavigate()

    const handleCancelar = async () => await cancelarPropuesta(intercambio.id)
    const handleNavigate = () => navigate(`/intercambios/${intercambio.id}`)
    const handleCalificar = async () => await calificarPerfil()

    return (
        <SectionCard >
            <SectionCard.Section >
                <HeaderUsuarioEstado
                    estado={estado}
                    destinatario={intercambio.destinatario}/>
            </SectionCard.Section>

            <SectionCard.Section >
                <div className="row mt-2">
                    <div className="col">
                        <small className="text-uppercase text-muted fw-semibold">
                            Vos pedís
                        </small>

                        {izq.map((f) => (
                            <ChipFigurita key={f.id} figurita={f} />
                        ))}
                    </div>

                    <div className="col-auto d-flex align-items-center">⇄</div>

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

            <SectionCard.Section >
                <div className="d-flex gap-2 mt-3">
                    <Button
                        className={"w-100"}
                        label={"Ver detalle"}
                        onClick={() => navigate(`/intercambios/${intercambio.id}`)}
                    />

                    {estado === "PENDIENTE" && (
                        <Button
                            className={"w-50"}
                            label={"Cancelar"}
                            onClick={handleCancelar}
                        />
                    )}

                    {estado === "ACEPTADO" && (
                        <Button
                            className={"w-50"}
                            label={"⭐ Calificar usuario"}
                            onClick={handleCalificar}
                        />
                    )}
                </div>
            </SectionCard.Section>
        </SectionCard>


    );
};

export default IntercambioCard;