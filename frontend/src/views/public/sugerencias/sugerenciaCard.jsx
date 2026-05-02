import Button from "../../../components/ui/button/button.jsx";
import styles from "./sugerenciaCard.module.css";
import FiguritaRecomendadaCard from "./figuritaRecomendadaCard.jsx";

const SugerenciaCard = ({perfil, figuritasRecomendadas, figuritasNecesarias}) => {
    return (
        <div className={`p-3 ${styles.card}`}>

            {/* HEADER */}
            <div className="d-flex align-items-center justify-content-between">

                <div className="d-flex align-items-center gap-2">
                    <div className={styles.avatar}>{perfil.nombre.slice(0,1).toUpperCase()}</div>
                    <p>{perfil.nombre}</p>
                </div>
            </div>

            <hr className="my-3"/>

            <div className="d-flex flex-column justify-content-between">
                <div className="d-flex flex-row justify-content-between gap-2">
                    <div className="small text-muted mb-1">VOS LE DAS</div>

                    <div className="small text-muted mb-1">ÉL TE DA</div>
                </div>
                <div className="d-flex flex-row align-items-center gap-2">
                    <div className="flex-grow-1">
                        {figuritasRecomendadas.map(fig =>
                            <FiguritaRecomendadaCard fig={fig} key={fig.id}/>
                        )}
                    </div>

                    <div className={styles.swapIcon}>
                        ⇄
                    </div>

                    <div className="flex-grow-1 text-end">

                        {figuritasNecesarias.map(fig =>
                            <FiguritaRecomendadaCard fig={fig} key={fig.id} verde={false}/>
                        )}
                    </div>
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