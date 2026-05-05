import styles from "../views/public/ver-subasta/oferta-card.module.css";

export const mostrar_label = (propuesta) => {
    return propuesta.figuritas_ofrecidas.map((fig, index) => (
        <span key={index} className={styles.figuritaOfrecida}>
                            {index > 0 && " + "}
            {fig.jugador} #{fig.numero}
                        </span>
    ))
}
