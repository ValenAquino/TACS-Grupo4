import SugerenciaCard from "../../sugerencia-card.jsx";
import {useCallback, useEffect, useState} from "react";
import {buscarSugerencias} from "../../../../../services/perfilService.js";
import ExtraInfo from "../../../../../components/ui/extra-info/extra-info.jsx";

const UnoAUnoSugerencias = () => {

    const [cargando, setCargando] = useState(true)
    const [sugerencias, setSugerencias] = useState([])

    const cargarSugerencias = useCallback(async () => {
        try {
            setCargando(true)
            const payload = await buscarSugerencias({userId:1001, tipo: "1a1"})
            setSugerencias(payload)

        } catch (error) {
            console.log(error)
        } finally {
            setCargando(false)
        }
    })

    useEffect(() => {
        cargarSugerencias()
    }, []);
    /*
        perfil:{
            id; (string)
            nombre; (string)
            }
        figuritasRecomendadas: [{
            id: (string)
            numero: (string)
            jugador: (string)
            seleccion: (string)
        }],
        figuritasNecesarias:[{
            id: (string)
            numero: (string)
            jugador: (string)
            seleccion: (string)
        }]
    */

    const mostrarSugerencias = () => {
        return (
            <>
                <ExtraInfo>
                    <p>Sugerencias de intercambio uno a uno, para obtener dicha figurita solo se necesita una de las tuyas!</p>
                </ExtraInfo>
                {
                    sugerencias.length > 0 ?
                        sugerencias.map(s => <SugerenciaCard key={s.perfil.id} perfil={s.perfil}
                                                             figuritasNecesarias = {s.figuritas_necesarias}
                                                             figuritasRecomendadas = {s.figuritas_recomendadas}/>
                        ) : <h2>No pudimos encontrar sugerencias!</h2>
                }
            </>
        )
    }

    return (
        <div className="d-flex flex-column gap-3 ms-2 me-2">
            {cargando ? <h2>Cargando sugerencias...</h2> : mostrarSugerencias()}
        </div>
    )
}

export default UnoAUnoSugerencias