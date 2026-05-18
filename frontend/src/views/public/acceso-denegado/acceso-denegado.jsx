import Button from "@/components/ui/button/button.jsx";
import {useNavigate} from "react-router-dom";

const AccesoDenegado = () => {
    const navigate = useNavigate()
    return (
        <div className="container text-center mt-5">
            <h1>No autorizado</h1>
            <p>Necesitás iniciar sesión para acceder a esta página.</p>
            <div className="d-flex justify-content-center align-items-center gap-3 mt-3">
                <Button
                    label={"Registrarse"}
                    onClick={() => {navigate("/registrar")}}
                />
                <Button
                    label={"Iniciar Sesion"}
                    onClick={() => {navigate("/login")}}
                />
            </div>
        </div>
    );
}

export default AccesoDenegado;