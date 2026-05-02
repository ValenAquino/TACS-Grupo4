import styles from "./sugerenciaCard.module.css";

const FiguritaRecomendadaCard = ({fig, verde=true}) => {
    return (
        <div key={fig.id} className={(verde ? styles.itemGreen : styles.itemBlue) + " d-flex flex-row justify-content-between mb-2"}>
            <div >
                {fig.jugador}
            </div>
            <p className="text-black">
                {fig.seleccion}
            </p>
        </div>
    )
}

export default FiguritaRecomendadaCard