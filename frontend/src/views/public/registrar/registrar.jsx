import { useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import {crearUsuario} from "@/services/usuarioService.js";
import {useToast} from "@/contexts/toastContext.jsx";
import {useAuth} from "@/contexts/userContext.jsx";
import {useError} from "@/contexts/errorContext.jsx";
import ModalInformativo from "@/components/ui/modales/modal-informativo/modal-informativo.jsx";

function Registrar() {
    const [formData, setFormData] = useState({
        nombre: "",
        contrasenia: "",
        confirmarContrasenia: ""
    });

    const {handleError, errorTemplate} = useError();

    const [errorState, setErrorState] = useState(errorTemplate({nombre:undefined, contrasenia: undefined}));
    const [onSubmit, setOnSubmit] = useState(false);

    const {showToast} = useToast();
    const {asignarUsuario} = useAuth()
    const navigate = useNavigate()

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (
            !formData.nombre ||
            !formData.contrasenia ||
            !formData.confirmarContrasenia
        ) {
            showToast("Completa todos los campos", "error");
            return;
        }

        if (formData.contrasenia !== formData.confirmarContrasenia) {
            showToast("Las contraseñas no coinciden", "error");
            return;
        }

        const usuario = {
            nombre: formData.nombre,
            contrasenia: formData.contrasenia
        };

        try {
            setOnSubmit(true);
            await crearUsuario(usuario)
            showToast(`Usuario creado correctamente`)
            navigate("/")
        } catch (error) {
            showToast(handleError(error, setErrorState), "error")
        } finally {
            setOnSubmit(false);
        }

    };

    return (
        <div
            className="container-fluid d-flex justify-content-center align-items-center py-5"
            style={{
                minHeight: "100vh",
                backgroundColor: "var(--color-background)"
            }}
        >
            <div
                className="card shadow p-4"
                style={{
                    width: "100%",
                    maxWidth: "450px",
                    backgroundColor: "var(--color-primary)",
                    border: "var(--border-weight) solid var(--border-color-light)",
                    borderRadius: "16px"
                }}
            >
                <h1
                    className="text-center mb-2"
                    style={{
                        color: "var(--color-secondary)"
                    }}
                >
                    Registrarse
                </h1>

                <p
                    className="text-center mb-4"
                    style={{
                        color: "var(--color-subtitle)"
                    }}
                >
                    Creá una cuenta nueva
                </p>

                <form onSubmit={handleSubmit}>

                    <div className="mb-3">
                        <label className="form-label">
                            Nombre
                        </label>

                        <input
                            type="text"
                            name="nombre"
                            className="form-control"
                            placeholder="Juan Pérez"
                            value={formData.nombre}
                            onChange={handleChange}
                            style={{
                                borderColor: "var(--border-color-dark)"
                            }}
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">
                            Contraseña
                        </label>

                        <input
                            type="password"
                            name="contrasenia"
                            className="form-control"
                            placeholder="********"
                            value={formData.contrasenia}
                            onChange={handleChange}
                            style={{
                                borderColor: "var(--border-color-dark)"
                            }}
                        />
                    </div>

                    <div className="mb-4">
                        <label className="form-label">
                            Confirmar contraseña
                        </label>

                        <input
                            type="password"
                            name="confirmarContrasenia"
                            className="form-control"
                            placeholder="********"
                            value={formData.confirmarContrasenia}
                            onChange={handleChange}
                            style={{
                                borderColor: "var(--border-color-dark)"
                            }}
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn w-100 fw-bold"
                        style={{
                            backgroundColor: "var(--color-secondary)",
                            color: "white"
                        }}
                    >
                        Crear cuenta
                    </button>

                </form>

                <div className="text-center mt-4">
                    <p>
                        ¿Ya tenés cuenta?{" "}
                        <Link
                            to="/login"
                            style={{
                                color: "var(--color-terciary)",
                                textDecoration: "none",
                                fontWeight: "600"
                            }}
                        >
                            Iniciar sesión
                        </Link>
                    </p>
                </div>
            </div>
            <ModalInformativo open={onSubmit}>
                <h3>Registrando usuario...</h3>
                <p>Esto puede tardar unos segundos</p>
            </ModalInformativo>
        </div>
    );
}

export default Registrar;