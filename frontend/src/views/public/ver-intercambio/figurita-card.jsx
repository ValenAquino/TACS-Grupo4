import styles from "./ver-intercambio.module.css";

const FiguritaCard = ({ figurita }) => {

    if (!figurita) return null;

    return (
        <div className={styles.figuritaCard}>
            <div className={styles.numeroFiguritaGrande}>
                #{figurita.numero}
            </div>

            <div className="mt-3 text-center">

                <h5 className="mb-1">
                    {figurita.jugador}
                </h5>

                <p className="text-muted mb-0">
                    {figurita.seleccion}
                </p>

                <small className="text-muted">
                    {figurita.posicion}
                </small>

            </div>
        </div>
    );
};

export default FiguritaCard;