import SugerenciaCard from "../../sugerenciaCard.jsx";
import {useCallback, useEffect, useState} from "react";
import {buscarSugerencias} from "../../../../../services/perfilService.js";

const TodosSugerencias = () => {

    const [cargando, setCargando] = useState(true)
    const [sugerencias, setSugerencias] = useState([])

    const cargarSugerencias = useCallback(async () => {
        try {
            setCargando(true)
            const payload = await buscarSugerencias()
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

    return (
        <div>
            {
                sugerencias.map(s => <SugerenciaCard key={s.perfil.id} figuritasNecesarias = {s.figuritasNecesarias}
                                                  figuritasRecomendadas = {s.figuritasRecomendadas}/>
            )}
            <SugerenciaCard
                perfil={{nombre: "Rodrigo_Lopez"}}
                figuritasRecomendadas={[{id:1, jugador:"#10 Messi", seleccion:"ARG"},{id:3, jugador:"#10 otroMessi", seleccion:"ARG"}]}
                figuritasNecesarias={[{id:2, jugador:"#-1 Nicolas Cage", seleccion:"UNK"}]}/>
        </div>
    )
}

export default TodosSugerencias