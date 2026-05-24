import Button from "@/components/ui/button/button.jsx";
import {useNavigate} from "react-router-dom";
import { useAuth } from '@/contexts/userContext.jsx'

const AccesoDenegado = () => {
    const navigate = useNavigate()
    const { user } = useAuth()

    return (
        <div className="container text-center mt-5">
            <h1>No autorizado</h1>
            {user ?
                <p>No tenes los privilegios requeridos para acceder a este servicio.</p>
              :<>
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
              </>
            }
        </div>
    );
}

export default AccesoDenegado;