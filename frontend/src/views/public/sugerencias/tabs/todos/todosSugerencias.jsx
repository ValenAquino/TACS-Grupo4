import SugerenciaCard from "../../sugerenciaCard.jsx";
import {useCallback, useEffect, useState} from "react";
import {buscarSugerencias} from "../../../../../services/perfilService.js";

const TodosSugerencias = () => {

    const [cargando, setCargando] = useState(true)
    const [sugerencias, setSugerencias] = useState([])

    const cargarSugerencias = useCallback(async () => {
        try {
            setCargando(true)
            const payload = await buscarSugerencias({userId:1000})
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

export default TodosSugerencias