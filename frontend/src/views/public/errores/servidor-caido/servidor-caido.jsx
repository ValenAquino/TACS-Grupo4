import Button from "@/components/ui/button/button.jsx";
import { useLocation, useNavigate } from "react-router-dom";
import {ping} from "@/services/api";

const ServidorCaido = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const previousPage = location.state?.from || "/";

    const reintentar = async () => {
        try {
            await ping()
            navigate(previousPage);
        } catch {
            // sigue caído: quedarse en la misma página
        }
    };

    return (
        <div className="container text-center mt-5">
            <h1>El servidor está caído...</h1>

            <div className="d-flex justify-content-center align-items-center gap-3 mt-3">
                <Button
                    label="Reintentar"
                    onClick={reintentar}
                />
            </div>
        </div>
    );
};

export default ServidorCaido;