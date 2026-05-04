import styles from "./perfil-simple.module.css";
import {useNavigate} from "react-router";

const PerfilSimple = ({perfil}) => {

    const navigate = useNavigate()

    const renderStars = (rating) => {
        return (
            <div className={styles.stars}>
                {[1,2,3,4,5].map((i) => (
                    <span key={i} className={i <= rating ? styles.filled : styles.empty}>
                    ★
                </span>
                ))}
            </div>
        )
    }

    return (
        <div className={styles.avatarContainer + " d-flex align-items-center gap-2"} onClick={() => navigate(`/perfil/${perfil.id}`)}>
            <div className={styles.avatar}>{perfil.nombre.slice(0,1).toUpperCase()}</div>
            <div>
                <h5 className="m-0">{perfil.nombre}</h5>
                <div className="fs-6 d-flex align-items-center gap-2">
                    {renderStars(perfil.puntuacion)}
                    <span>{perfil.puntuacion}</span>
                </div>
            </div>
        </div>
    )
}

export default PerfilSimple;