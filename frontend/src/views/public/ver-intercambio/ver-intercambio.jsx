import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { useNavigate } from "react-router";
import Breadcrumb from "../../../components/ui/breadcrumb/breadcrumb.jsx";
import SectionCard from "../../../components/ui/section-card/section-card.jsx";
import SectionTitle from "../../../components/ui/section-title/section-title.jsx";
import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import Button from "../../../components/ui/button/button.jsx";

import {
    aceptarPropuesta,
    buscarPropuestas,
    rechazarPropuesta,
} from "../../../services/propuestasService.js";

import FiguritaCard from "./figurita-card.jsx";
import OfertaCard from "./oferta-card.jsx";

import styles from "./ver-intercambio.module.css";

const VerIntercambio = () => {

    const { intercambioId } = useParams();
    const navigate = useNavigate();
    const [cargando, setCargando] = useState(true);
    const [propuesta, setPropuesta] = useState(null);

    const cargarIntercambio = async () => {
        try {

            setCargando(true);

            const response = await buscarPropuestas({
                pagina: 0,
                limite: 100,
                tipo: "RECIBIDAS"
            });

            const encontrada = response.contenido.find(
                (p) => p.id.toString() === intercambioId
            );

            setPropuesta(encontrada);
            //console.log(encontrada)

        } catch (e) {
            console.error(e);
        } finally {
            setCargando(false);
        }
    };

    useEffect(() => {
        cargarIntercambio();
    }, []);

    const handleAceptar = async () => {
        //console.log("CLICK ACEPTAR");
        try {
            await aceptarPropuesta(propuesta.id);
            navigate("/intercambios");
        } catch (e) {
            console.error(e);
        }
    };

    const handleRechazar = async () => {
        try {
            await rechazarPropuesta(propuesta.id);
            navigate("/intercambios");
        } catch (e) {
            console.error(e);
        }
    };

    if (cargando) {
        return (
            <div className="container py-4">
                <h3>Cargando intercambio...</h3>
            </div>
        );
    }

    if (!propuesta) {
        return (
            <div className="container py-4">
                <h3>No se encontró el intercambio</h3>
            </div>
        );
    }

    return (
        <div className="container py-4 px-3 px-md-4">

            <Breadcrumb
                crumbs={[
                    { name: "Intercambios", to: "/intercambios" },
                    { name: `#${propuesta.id}`, to: `/intercambios/${propuesta.id}` },
                ]}
            />

            <SectionCard>

                <SectionTitle>
                    ESTADO DEL INTERCAMBIO
                </SectionTitle>

                <SectionCard.Section>

                    <div className="d-flex justify-content-between align-items-center">

                        <div>
                            <small className="text-muted">
                                Estado actual
                            </small>

                            <h4 className="mb-0">
                                {propuesta.estado?.at(-1)?.valor ?? "PENDIENTE"}
                            </h4>
                        </div>

                        <div>
                            <Button
                                label={"Aceptar"}
                                className={"me-2"}
                                onClick={handleAceptar}
                            />

                            <Button
                                label={"Rechazar"}
                                onClick={handleRechazar}
                            />
                        </div>

                    </div>

                </SectionCard.Section>

            </SectionCard>

            <SectionCard>

                <SectionTitle>
                    USUARIO
                </SectionTitle>

                <SectionCard.Section>
                    <PerfilSimple perfil={propuesta.autor} />
                </SectionCard.Section>

            </SectionCard>

            <SectionCard>

                <SectionTitle>
                    FIGURITA SOLICITADA
                </SectionTitle>

                <SectionCard.Section>

                    <FiguritaCard
                        figurita={propuesta.figurita_buscada}
                    />

                </SectionCard.Section>

            </SectionCard>

            <SectionCard>

                <SectionTitle>
                    FIGURITAS OFRECIDAS ({propuesta.figuritas_ofrecidas.length})
                </SectionTitle>

                <SectionCard.Section>

                    <div className="d-flex flex-column gap-3">

                        {propuesta.figuritas_ofrecidas.map((fig) => (
                            <FiguritaCard
                                key={fig.id}
                                figurita={fig}
                            />
                        ))}

                    </div>

                </SectionCard.Section>

            </SectionCard>

            <SectionCard>

                <SectionTitle>
                    RESUMEN DE LA PROPUESTA
                </SectionTitle>

                <SectionCard.Section>

                    <OfertaCard propuesta={propuesta} />

                </SectionCard.Section>

            </SectionCard>

        </div>
    );
};

export default VerIntercambio;