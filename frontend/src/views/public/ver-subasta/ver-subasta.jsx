import styles from "./ver-subasta.module.css"
import {useParams} from "react-router";
import {useEffect, useState} from "react";
import {buscarSubasta} from "../../../services/subastasService.js";
import Breadcrumb from "../../../components/ui/breadcrumb/breadcrumb.jsx";
import SectionCard from "../../../components/ui/section-card/section-card.jsx";
import SectionTitle from "../../../components/ui/section-title/section-title.jsx";
import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import OfertaCard from "./oferta-card.jsx";
import TuOfertaCard from "./tu-oferta-card.jsx";
import Button from "../../../components/ui/button/button.jsx";
import useUsuarioActual from "../../../hooks/useUsuarioActual.js";
import {useNavigate} from "react-router-dom";

const VerSubasta = () => {
    const {subId} = useParams()
    const {userId} = useUsuarioActual()
    const [cargando, setCargando] = useState(true)
    const [subasta, setSubasta] = useState(undefined)
    const [tiempo, setTiempo] = useState(0)
    const [subastaAbierta, setSubastaAbierta] = useState(false)
    const navigate = useNavigate()

    const procesarDuracion = () => {

        const horas = Math.floor(tiempo / 3600)
        const minutos = Math.floor((tiempo % 3600) / 60)
        const segundos = tiempo % 60

        return `${horas.toString().padStart(2, "0")}:${minutos.toString().padStart(2, "0")}:${segundos.toString().padStart(2, "0")}`
    }

    const calcularDuracionTotal = () => {
        const inicio = new Date(subasta.inicio)
        const cierre = new Date(subasta.cierre)

        const diffMs = cierre - inicio

        const horas = Math.floor(diffMs / (1000 * 60 * 60))
        const minutos = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))

        return `${horas}h ${minutos}m`
    }

    const formatearFecha = (fecha) => {
        const f = new Date(fecha)

        const dia = f.getDate()
        const mes = f.toLocaleString('es-AR', {month: 'short'}) // abr, may, etc
        const horas = f.getHours().toString().padStart(2, "0")
        const minutos = f.getMinutes().toString().padStart(2, "0")

        return `${dia} ${mes}, ${horas}:${minutos}`
    }

    const mostrarOfertaDeUsuario = (ofertas) => {
        const ofertaPropia = ofertas.find(o => o.autor.usuario_id === userId.toString()) //Mismo Id que la sesion
        return ofertaPropia !== undefined ? <TuOfertaCard oferta={ofertaPropia} subastaAbierta={subastaAbierta}/> :
            subastaAbierta &&
                <div className={"d-flex flex-row justify-content-center align-items-center gap-2"}>
                    <p>¿Aún no ofertaste?</p>
                    <Button onClick={() => navigate(`/subastas/${subId}/nuevaOferta`)}>Proponer Oferta</Button>
                </div>
    }

    const cargarSubasta = async () => {
        try {
            setCargando(true)
            const payload = await buscarSubasta({subId});
            setSubasta(payload)
            setSubastaAbierta(new Date(payload.cierre) > new Date())
            setTiempo(payload.tiempo_restante)
        } catch (err) {
            console.log(err)
        } finally {
            setCargando(false)
        }
    }

    useEffect(() => {
        cargarSubasta()
    }, []);

    useEffect(() => {
        if (!tiempo) return

        const interval = setInterval(() => {
            setTiempo(prev => Math.max(prev - 1, 0))
        }, 1000)

        return () => clearInterval(interval)
    }, [tiempo])

    const mostrarSubasta = () => {
        return (
            <>
                <div
                    className={styles.figuritaSubastada + " p-2 d-flex flex-column justify-content-center align-items-center gap-2 w-100 rounded-2 mb-3"}>
                    <div className={styles.figuritaImagen + " bg-white rounded-3 "}>
                    </div>

                    <h4 className={"text-white"}>{subasta.figurita.jugador}</h4>
                    <h6 className={"text-white"}>{subasta.figurita.seleccion}</h6>

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
                        <div className="d-flex flex-column gap-3">

                            {/* FILA 1 */}
                            <div className="d-flex">
                                <div className="w-50">
                                    <h5>Inicio</h5>
                                    <p>{formatearFecha(subasta.inicio)}</p>
                                </div>
                                <div className="w-50">
                                    <h5>Cierre</h5>
                                    <p>{formatearFecha(subasta.cierre)}</p>
                                </div>
                            </div>

                            {/* FILA 2 */}
                            <div className="d-flex">
                                <div className="w-50">
                                    <h5>Duración</h5>
                                    <p>{calcularDuracionTotal()}</p>
                                </div>
                                <div className="w-50">
                                    <h5>Ofertas recibidas</h5>
                                    <p>{subasta.ofertas.length} ofertas</p>
                                </div>
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


                {subasta.figuritas_solicitadas.length > 0 &&
                    subasta.calificacion_minima > 0 &&
                    subasta.calificacion_minima <= 5 &&
                    <SectionCard>
                        <SectionTitle>CONDICIONES PARA OFERTAR</SectionTitle>
                        <SectionCard.Section>
                            <ul>
                                {subasta.figuritas_solicitadas.length > 0 &&
                                    <li>Requiere de una de estas figuritas: {
                                        subasta.figuritas_solicitadas.map((fd, index) => <p
                                            key={index}>{fd.jugador}</p>)
                                    }</li>
                                }
                                {subasta.calificacion_minima > 0 && subasta.calificacion_minima <= 5 &&
                                    <li>
                                        Requiere de calificacion
                                        minima: {subasta.calificacion_minima}
                                    </li>
                                }
                            </ul>
                        </SectionCard.Section>
                    </SectionCard>
                }


                <SectionCard>
                    <SectionTitle>OFERTAS {subastaAbierta ? "ACTUALES": "HISTORICAS"} ({subasta.ofertas.length})</SectionTitle>
                    <SectionCard.Section>
                        <div className="d-flex flex-column gap-2">
                            {subasta.ofertas.length > 0 ?
                                subasta.ofertas.map((oferta, index) =>
                                    <OfertaCard key={index} position={index + 1} propuesta={oferta}/>)
                                : <h4>Aún no hay ofertas!</h4>
                            }

                        </div>
                    </SectionCard.Section>
                    {
                        subasta.perfil.usuario_id !== userId && subastaAbierta ?
                            <>
                                <SectionTitle>TU OFERTA</SectionTitle>
                                <SectionCard.Section>
                                    {mostrarOfertaDeUsuario(subasta.ofertas)}
                                </SectionCard.Section>
                            </>: null
                    }
                </SectionCard>
            </>
        )
    }

    return (
        <div className="container py-4 px-3 px-md-4">
            <Breadcrumb crumbs={[
                {name: 'Subasta', to: '/subastas'},
                {name: `#${subId}`, to: `/subastas/${subId}`},
            ]}/>
            {cargando ? <h2>Cargando subasta...</h2> : mostrarSubasta()}
        </div>
    )
}

export default VerSubasta;