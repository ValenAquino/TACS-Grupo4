import Button from "../../../components/ui/button/button.jsx";
import styles from "./sugerencia-card.module.css";
import FiguritaRecomendadaCard from "./figurita-recomendada-card.jsx";
import PerfilSimple from "../../../components/ui/perfil-simple/perfil-simple.jsx";

const SugerenciaCard = ({perfil, figuritasRecomendadas, figuritasNecesarias}) => {


    return (
        <div className={`p-3 ${styles.card}`}>

            {/* HEADER */}
            <div className="d-flex align-items-center justify-content-between">

                <PerfilSimple perfil={perfil}/>
            </div>

            <hr className="my-3"/>

            <div className="d-flex flex-column justify-content-between">
                <div className="d-flex flex-row justify-content-between gap-2">
                    <div className="small text-muted mb-1">A ÉL LE INTERESA</div>

                    <div className="small text-muted mb-1">ÉL TIENE</div>
                </div>
                <div className="d-flex flex-row align-items-center gap-2">
                    <div className="flex-grow-1">
                        {figuritasNecesarias.map(fig =>
                            <FiguritaRecomendadaCard fig={fig} key={fig.id}/>
                        )}
                    </div>

                    <div className={styles.swapIcon}>
                        ⇄
                    </div>

                    <div className="flex-grow-1 text-end">

                        {figuritasRecomendadas.map(fig =>
                            <FiguritaRecomendadaCard fig={fig} key={fig.id} verde={false}/>
                        )}
                    </div>
                </div>
            </div>

            <hr className="my-3"/>

            <div className="d-flex justify-content-end align-items-center">

                <div className="d-flex gap-2">
                    <Button>Proponer intercambio</Button>
                </div>
            </div>
        </div>
    );
};

export default SugerenciaCard;