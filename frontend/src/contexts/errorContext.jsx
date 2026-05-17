import { createContext, useContext } from "react";
import { useNavigate } from "react-router-dom";

const ErrorContext = createContext(null);

export const ErrorProvider = ({ children }) => {
    const navigate = useNavigate();

    const errorTemplate = (fields = undefined) => {

        return {
            codigo: undefined,
            mensaje: undefined,
            errors: fields
        }
    }

    const handleError = (error, setter) => {
        switch (error.type) {

            case "SERVER_DOWN":
                navigate("/server-down");
                break;

            case "FORBIDDEN":
                navigate("/unauthorized");
                break;


            default: {
                const errorProcesado = {
                    codigo: error.code || error.status,
                    mensaje: error.message,
                    // errors: error.errors,
                }

                setter(errorProcesado)
                return errorProcesado.mensaje
            }
        }
    };

    return (
        <ErrorContext.Provider value={{ handleError, errorTemplate }}>
            {children}
        </ErrorContext.Provider>
    );
};

export const useError = () => useContext(ErrorContext);