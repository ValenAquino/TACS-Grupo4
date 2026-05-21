import {Outlet} from "react-router-dom";
import {Navigate} from "react-router";
import {useAuth} from "@/contexts/userContext.jsx";

const RutaProtegida = () => {
    const { user } = useAuth();

    return user
        ? <Outlet />
        : <Navigate to="/acceso-denegado" replace />;
}

export default RutaProtegida;