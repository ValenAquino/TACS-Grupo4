import Breadcrumb from "../../../components/ui/breadcrumb/breadcrumb.jsx";
import StatCard from "../../../components/ui/statcard/statcard.jsx";
import styles from './sugerencias.module.css';

const Sugerencias = () => {
    return (
        <div>
            <Breadcrumb crumbs={[{name: "Explorar", to: "/explorar"}, {name: "Sugerencias", to: "/sugerencias"}]}/>

            <h2>Sugerencias</h2>
            <h4>Coincidencias entre tus faltantes y los repetidos de otros usuarios, y viceversa</h4>
            <hr/>

            <div className="d-flex flex-column flex-nowrap gap-3">
                <div
                    className={styles.statGrid + " d-grid gap-3"}
                >
                    <StatCard title="COINCIDENCIAS TOTALES" value="12" />
                    <StatCard title="TUS FALTANTES" value="47" />
                    <StatCard title="TUS REPETIDAS" value="23" />
                </div>
                <div className="d-flex flex-row flex-nowrap gap-3 bg-body-secondary p-3 rounded-3">
                    <i className={styles.informationIcon + " bi bi-info-circle"}></i>
                    <div>
                        <h6><strong>¿Cómo funciona?</strong></h6>
                        <p>El sistema analiza tus figuritas faltantes y repetidas, y las cruza con las colecciones
                            de todos los usuarios.</p>
                    </div>
                </div>
            </div>

        </div>
    )
}

export default Sugerencias