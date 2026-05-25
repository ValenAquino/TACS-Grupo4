import Button from "@/components/ui/button/button.jsx";
import {useNavigate} from "react-router";

const ErrorInterno = () => {
    const navigate = useNavigate();

    return (
        <div className="container text-center mt-5">
            <h1 className="text-dark">Error interno del servidor</h1>

            <p className="text-dark mt-3">
                Ocurrió un problema inesperado y no pudimos procesar tu solicitud.
                Intentá nuevamente en unos momentos.
            </p>

            <div className="d-flex justify-content-center align-items-center gap-3 mt-4">
                <Button
                    label={"Volver al inicio"}
                    onClick={() => navigate("/explorar")}
                />
            </div>
        </div>
    )
}

export default ErrorInterno