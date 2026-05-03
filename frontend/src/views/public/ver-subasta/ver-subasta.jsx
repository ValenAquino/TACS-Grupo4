import styles from "./ver-subasta.module.css"
import {useParams} from "react-router";
import {useEffect, useState} from "react";
import {buscarSubasta} from "../../../services/subastaService.js";
import Breadcrumb from "../../../components/ui/breadcrumb/breadcrumb.jsx";
import SectionCard from "../../../components/ui/section-card/section-card.jsx";
import SectionTitle from "../../../components/ui/section-title/section-title.jsx";
import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import OfertaCard from "./oferta-card.jsx";

const VerSubasta = () => {
    const subId = useParams()
    const [cargando, setCargando] = useState(true)
    const [subasta, setSubasta] = useState(undefined)

    const procesarDuracion = () => {
        const horas = Math.floor(subasta.duracion / 60)

        const minutos = subasta.duracion - horas * 60

        const segundos = (subasta.duracion - Math.floor(subasta.duracion)) * 60

        return `${horas.toString().padStart(2,"0")}:${minutos.toString().padStart(2,"0")}:${segundos.toString().padStart(2,"0")}`
    }

    const cargarSubasta = async () => {
        try {
            setCargando(true)
            const payload = await buscarSubasta(subId);
            setSubasta(payload)
        } catch (err){
            console.log(err)
        } finally {
            setCargando(false)
        }
    }

    useEffect(() => {
        cargarSubasta()
    }, []);

    const mostrarSubasta = () => {
        return (
            <>
                <div className={styles.figuritaSubastada + " p-2 d-flex flex-column justify-content-center align-items-center gap-2 w-100 rounded-2 mb-3"}>
                    <div className={styles.figuritaImagen + " bg-white rounded-3 "}>
                    </div>

                    <h4>{subasta.figurita.jugador}</h4>
                    <h6>{subasta.figurita.seleccion}</h6>

                </div>
               <SectionCard>
                   <SectionTitle>TIEMPO RESTANTE</SectionTitle>
                   <SectionCard.Section>
                       <div className="d-flex flex-row align-items-end gap-2">
                           <h2>{procesarDuracion()}</h2>
                           <p>HH:MM:SS</p>
                       </div>
                   </SectionCard.Section>
               </SectionCard>

                <SectionCard>
                    <SectionTitle>DETALLES DE LA SUBASTA</SectionTitle>
                    <SectionCard.Section>
                        <div className="d-flex flex-row  flex-wrap">
                            <div className="d-flex justify-content-between flex-row w-100">
                                <p className="w-50">Inicio</p>
                                <p className="w-50">Cierre</p>
                            </div>
                            <div className="d-flex justify-content-between flex-row w-100">
                                <p className="w-50">Duracion</p>
                                <p className="w-50">Ofertas</p>
                            </div>
                        </div>
                    </SectionCard.Section>
                </SectionCard>

                <SectionCard>
                    <SectionTitle>PUBLICADO POR</SectionTitle>
                    <SectionCard.Section>
                        <PerfilSimple perfil={subasta.perfil}/>
                    </SectionCard.Section>
                </SectionCard>

                <SectionCard>
                    <SectionTitle>CONDICIONES PARA OFERTAR</SectionTitle>
                    <SectionCard.Section>
                        ??
                    </SectionCard.Section>
                </SectionCard>

                <SectionCard>
                    <SectionTitle>OFERTAS ACTUALES ({subasta.ofertas.length})</SectionTitle>
                    <SectionCard.Section>
                        <div className="d-flex flex-column gap-2">
                            {subasta.ofertas.map((oferta,index) => <OfertaCard key={index} position={index+1} propuesta={oferta} />)}
                        </div>
                    </SectionCard.Section>
                    <SectionCard.Section>
                        <div className="w-100 h-100">a</div>
                    </SectionCard.Section>
                </SectionCard>
            </>
        )
    }

    return (
        <div>
            <Breadcrumb crumbs={[
                {name: 'Subasta', to: '/subastas'},
                {name: `#${subId}`, to: `/subastas/${subId}`},
            ]}/>
            {cargando ? <h2>Cargando subasta...</h2> : mostrarSubasta()}
        </div>
    )
}

export default VerSubasta;