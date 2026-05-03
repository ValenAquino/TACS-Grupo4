import Breadcrumb from "../../../components/ui/breadcrumb/breadcrumb.jsx";
import ContadorCard from "../../../components/ui/contador-card/contador-card.jsx";
import styles from './sugerencias.module.css';
import TabsContainer from "../../../components/ui/tabs-container/tabs-container.jsx";
import TodosSugerencias from "./tabs/todos/todos-sugerencias.jsx";
import {useCallback, useEffect, useState} from "react";
import {buscarContadores} from "../../../services/perfilService.js";
import UnoAUnoSugerencias from "./tabs/todos/1a1-sugerencias.jsx";
import ExtraInfo from "../../../components/ui/extra-info/extra-info.jsx";
import NAUnoSugerencias from "./tabs/todos/na1-sugerencias.jsx";
import UnoANSugerencias from "./tabs/todos/1an-sugerencias.jsx";

const Sugerencias = () => {

    const [cargando, setCargando] = useState(true)
    const [contadores, setContadores] = useState([])

    const TABS = [
        { key: 'todos', label: 'Todos', component: TodosSugerencias, props: {} },
        { key:"1a1", label: "1 a 1", component: UnoAUnoSugerencias, props: {} },
        { key:"na1", label: "N a 1", component: NAUnoSugerencias, props: {} },
        { key:"1an", label: "1 a N", component: UnoANSugerencias, props: {}}
    ];

    const cargarContadores = useCallback(async () => {
        try {
            setCargando(true)
            const payload = await buscarContadores({userId:1001})
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
                <ExtraInfo>
                    <h6 className="m-0"><strong>¿Cómo funciona?</strong></h6>
                    <p>El sistema analiza tus figuritas faltantes y repetidas, y las cruza con las colecciones
                        de todos los usuarios.</p>
                </ExtraInfo>

                <TabsContainer tabs={TABS}/>
            </div>
        </div>
    )
}

export default Sugerencias