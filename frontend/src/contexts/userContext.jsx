import {createContext, useContext, useEffect, useState} from "react";
import {buscarUsuario, logout} from "../services/sesionService.js";
import {useNavigate} from "react-router-dom";
// import {useError} from "./errorContext.jsx";
// import {useToast} from "./toastContext.jsx";

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
    // const {handleError} = useError();
    // const {showToast} = useToast();

    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem("sesion");
        return storedUser ? JSON.parse(storedUser) : undefined;
    });

    const navigate = useNavigate();

    const asignarUsuario = (user, reload = true) => {
        setUser(user)

        localStorage.setItem("sesion", JSON.stringify(user))

        if (reload) {
            navigate("/")
        }
    }

    const verificarSesion = async () => {
        try {
            await buscarUsuario((obj) => asignarUsuario (obj, false))

        } catch (error) {

            if (error.type === "UNAUTHORIZED") {
                setUser(undefined)
                localStorage.removeItem("user")
            }
        }
    }

    useEffect(() => {
        verificarSesion()
    }, [])

    const updateUser = (fields) => {
        const updatedUser = {...user, ...fields};

        localStorage.setItem("sesion", JSON.stringify(updatedUser))

        setUser(updatedUser)
    }

    const cerrarSesion = async () => {
        setUser(undefined)
        localStorage.removeItem("sesion")

        await logout()

        navigate("/")
    }

    const tieneSesion = () => {
        return user !== undefined
    }

    const returnValue = {
        user,
        asignarUsuario,
        cerrarSesion,
        tieneSesion
    }

    return (
        <AuthContext.Provider value={returnValue}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);