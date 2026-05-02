import Button from "../../../components/ui/button/button.jsx";
import styles from "./sugerenciaCard.module.css";

const SugerenciaCard = ({perfil, figuritasRecomendadas, figuritasNecesarias}) => {
    return (
        <div className={`p-3 ${styles.card}`}>

            {/* HEADER */}
            <div className="d-flex align-items-center justify-content-between">

                <div className="d-flex align-items-center gap-2">
                    <div className={styles.avatar}>RL</div>
                    <p>{perfil.nombre}</p>
                </div>
            </div>

            <hr className="my-3"/>

            <div className="d-flex align-items-end justify-content-between">
                <div className="flex-grow-1">
                    <div className="small text-muted mb-1">VOS LE DAS</div>
                    {figuritasRecomendadas.map(fig =>
                        <div key={fig.id} className={styles.itemGreen+ " d-flex flex-row justify-content-between"}>
                            <div >
                                {fig.jugador}
                            </div>
                            <p className="text-black">
                                {fig.seleccion}
                            </p>
                        </div>
                        )
                    }
                </div>

                <div className={styles.swapIcon}>
                    ⇄
                </div>

                <div className="flex-grow-1 text-end">
                    <div className="small text-muted mb-1">ÉL TE DA</div>

                    {figuritasNecesarias.map(fig =>
                        <div key={fig.id} className={styles.itemBlue + " d-flex flex-row justify-content-between"}>
                            <div >
                                {fig.jugador}
                            </div>
                            <p className="text-black">
                                {fig.seleccion}
                            </p>
                        </div>
                    )}
                </div>

            </div>

            <hr className="my-3"/>

            <div className="d-flex justify-content-end align-items-center">

                <div className="d-flex gap-2">
                    <Button>Ver perfil</Button>
                    <Button>Proponer intercambio</Button>
                </div>
            </div>
        </div>
    );
};

export default SugerenciaCard;