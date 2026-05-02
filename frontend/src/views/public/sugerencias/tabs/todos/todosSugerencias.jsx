import SugerenciaCard from "../../sugerenciaCard.jsx";

const TodosSugerencias = () => {
    return (
        <div>
            <SugerenciaCard
                perfil={{nombre: "Rodrigo_Lopez"}}
                figuritasRecomendadas={[{id:1, jugador:"#10 Messi", seleccion:"ARG"}]}
                figuritasNecesarias={[{id:2, jugador:"#-1 Nicolas Cage", seleccion:"UNK"}]}/>
        </div>
    )
}

export default TodosSugerencias