import styles from "./perfil-simple.module.css";
import {useNavigate} from "react-router";

const PerfilSimple = ({perfil}) => {

    const navigate = useNavigate()
    return (
        <div className={styles.avatarContainer + " d-flex align-items-center gap-2"} onClick={() => navigate(`/perfil/${perfil.id}`)}>
            <div className={styles.avatar}>{perfil.nombre.slice(0,1).toUpperCase()}</div>
            <div>
                <h5 className="m-0">{perfil.nombre}</h5>
                <p className="fs-6">Puntuacion media: {perfil.puntuacion}</p>
            </div>
        </div>
    )
}

export default PerfilSimple;