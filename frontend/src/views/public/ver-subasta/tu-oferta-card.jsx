import OfertaCard from "./oferta-card.jsx";
import Button from "../../../components/ui/button/button.jsx";

const TuOfertaCard = ({oferta}) => {
    return (
        <div className="d-flex flex-column gap-3">
            <OfertaCard propuesta={oferta} />
            <p>Podés mejorar tu oferta antes de que finalice!</p>
            <Button>Mejorar oferta</Button>
        </div>
    )
}
export default TuOfertaCard