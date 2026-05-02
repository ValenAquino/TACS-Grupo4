import Breadcrumb from "../../../components/ui/breadcrumb/breadcrumb.jsx";
import ContadorCard from "../../../components/ui/contador-card/contador-card.jsx";
import styles from './sugerencias.module.css';
import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";
import TodosSugerencias from "./tabs/todos/todos-sugerencias.jsx";
import {useCallback, useEffect, useState} from "react";
import {buscarContadores} from "../../../services/perfilService.js";

const Sugerencias = () => {

    const [cargando, setCargando] = useState(true)
    const [contadores, setContadores] = useState([])

    const TABS = [
        { key: 'todos', label: 'Todos', component: TodosSugerencias, props: {} }
    ];

    const cargarContadores = useCallback(async () => {
        try {
            setCargando(true)
            const payload = await buscarContadores({userId:1000})
            setContadores(payload)

        } catch (error) {
            console.log(error)
        } finally {
            setCargando(false)
        }
    })

    useEffect(() => {
        cargarContadores()
        /*
            stat: [{
                nombre: (string)
                 valor: (number)
            }]
        */
    }, []);

    const mostrarContadores = () => {
        return (
            <>
                {contadores.map((st, index) => <ContadorCard key={index} title={st.nombre} value={st.valor}/>)}
            </>
        )
    }

    return (
        <div className={styles.sugerenciasBody}>
            <Breadcrumb className={styles.left}
                        crumbs={[{name: "Explorar", to: "/explorar"}, {name: "Sugerencias", to: "/sugerencias"}]}/>

            <h2 className={styles.left}><strong>Sugerencias</strong></h2>
            <p className={styles.left + " fs-5 opacity-75"}>Coincidencias entre tus faltantes y los repetidos de otros usuarios, y viceversa</p>

            <hr/>

            <div className="d-flex flex-column flex-nowrap gap-3 w-100">
                <div
                    className={styles.statGrid + " d-grid gap-3"}
                >
                    {cargando ? <h2>Cargando estadisticas...</h2> : mostrarContadores()}
                </div>
                <div className="d-flex flex-row flex-nowrap gap-3 bg-body-secondary p-3 rounded-3">
                    <i className={styles.informationIcon + " bi bi-info-circle"}></i>
                    <div>
                        <h6><strong>¿Cómo funciona?</strong></h6>
                        <p>El sistema analiza tus figuritas faltantes y repetidas, y las cruza con las colecciones
                            de todos los usuarios.</p>
                    </div>
                </div>

                <TabsContainer tabs={TABS}/>
            </div>
        </div>
    )
}

export default Sugerencias