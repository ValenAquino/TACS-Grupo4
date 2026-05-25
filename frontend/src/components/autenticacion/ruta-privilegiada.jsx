import {Outlet} from "react-router-dom";
import {Navigate} from "react-router";
import {useAuth} from "@/contexts/userContext.jsx";

const RutaPrivilegiada = () => {
  const { user } = useAuth();

  return user && user.rol === "ADMINISTRADOR"
    ? <Outlet />
    : <Navigate to="/acceso-denegado" replace />;
}

export default RutaPrivilegiada;