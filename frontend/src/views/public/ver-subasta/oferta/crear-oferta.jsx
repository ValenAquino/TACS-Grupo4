import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { buscarSubasta } from "@/services/subastasService.js";
import FiguritaCard from "@/components/ui/figurita-card/figurita-card.jsx";
import SectionTitle from "@/components/ui/section-title/section-title.jsx";
import SectionCard from "@/components/ui/section-card/section-card.jsx";
import {buscarRepetidas} from "@/services/perfilService.js";
import useUsuarioActual from "@/hooks/useUsuarioActual.js";
import ScrollRepetidas from "@/components/ui/scroll-figuritas/scroll-repetidas.jsx";

const CrearOferta = () => {
    const {subId} = useParams();
    const [subasta, setSubasta] = useState(undefined);
    const [repetidas, setRepetidas] = useState(undefined);
    const [cargando, setCargando] = useState(true);
    const {userId} = useUsuarioActual()

    const cargarSubasta = async () => {
        try {
            setCargando(true);
            const payload_subasta = await buscarSubasta({subId});
            const payload_repetidas = await buscarRepetidas(userId)
            setSubasta(payload_subasta);
            setRepetidas(payload_repetidas)
        } catch (error) {
            console.error('Error al cargar la subasta:', error);
        } finally {
            setCargando(false);
        }
    }

    useEffect(() => {
        cargarSubasta();
    }, [subId]);

    const mostrarContenido = () => {
        return (
            <>
                <h2>Crear oferta para la subasta: #{subasta.id}</h2>
                <h3>Se requiere la figurita {subasta.figurita.jugador}</h3>

                <SectionCard>
                    <SectionTitle>Condiciones minimas a cumplir</SectionTitle>
                    <SectionCard.Section> 
                        {   subasta.figuritas_solicitadas.length > 0 ?
                            <>
                                <h5>Figuritas solicitadas:</h5>
                                <ul>
                                    {
                                        subasta.figuritas_solicitadas.map((figurita, index) => ( 
                                            <li key={index}>{index > 0 ?? " + "}{
                                                <FiguritaCard id={figurita.id} 
                                                number={figurita.numero} name={figurita.jugador} 
                                            />}</li>
                                        ))
                                    }
                                </ul>
                            </> : null
                        }
                        {
                            subasta.calificacion_minima_solicitada ?
                                <h5>Se requiere de un puntaje mayor a: {subasta.calificacion_minima_solicitada}</h5>
                                : null
                        }
                    </SectionCard.Section>
                </SectionCard>

                <ScrollRepetidas figuritas={repetidas} loading={cargando} />
            </>
        )
    } 

    return (
        <div>
            {cargando ? <h2>Cargando...</h2> : mostrarContenido()}
        </div>
    )
}
export default CrearOferta;