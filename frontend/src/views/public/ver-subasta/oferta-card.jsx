import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import styles from "./oferta-card.module.css"

const OfertaCard = ({propuesta, position=undefined}) => {
    return (
        <div className={styles.ofertaCard + " ps-3 pe-3 pt-1 pb-1 d-flex flex-row align-items-center gap-3"}>
            {position ?
                <div
                    className={styles.position + " p-3 d-flex align-items-center justify-content-center"}>
                    {position}°
                </div> : null
            }
            <div>
                <PerfilSimple perfil={propuesta.autor}></PerfilSimple>
                {
                    propuesta.figuritas_ofrecidas.map((fig, index) => (
                        <span key={index} className={styles.figuritaOfrecida}>
                            {index > 0 && " + "}
                            {fig.jugador} #{fig.numero}
                        </span>
                    ))
                }
            </div>
        </div>
    )
}

export default OfertaCard