import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";
import styles from "./oferta-card.module.css"

const OfertaCard = ({propuesta, position}) => {
    return (
        <div className={styles.ofertaCard + " ps-3 pe-3 pt-1 pb-1 d-flex flex-row align-items-center gap-3"}>
            <div className={styles.position + " p-3 d-flex align-items-center justify-content-center"}>
                {position}°
            </div>
            <div>
                <PerfilSimple perfil={propuesta.autor}></PerfilSimple>
                <p className={styles.figuritaOfrecida}>{propuesta.figurita_buscada.jugador} #{propuesta.figurita_buscada.numero}</p>
            </div>
        </div>
    )
}

export default OfertaCard