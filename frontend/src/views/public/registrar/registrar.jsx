import { useState } from "react";
import { Link } from "react-router-dom";
import {iniciarSesion, registrarUsuario} from "@/services/sesionService.js";

function Registrar() {
    const [formData, setFormData] = useState({
        nombre: "",
        password: "",
        confirmarPassword: ""
    });

    const [error, setError] = useState("");
    const [onSubmit, setOnSubmit] = useState(false);

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
            !formData.password ||
            !formData.confirmarPassword
        ) {
            setError("Completa todos los campos");
            return;
        }

        if (formData.password !== formData.confirmarPassword) {
            setError("Las contraseñas no coinciden");
            return;
        }

        setError("");

        const usuario = {
            nombre: formData.nombre,
            contrasenia: formData.password
        };

        try {
            setOnSubmit(true);
            await registrarUsuario(usuario)
        } catch (e) {
            setError(true)
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
                            name="password"
                            className="form-control"
                            placeholder="********"
                            value={formData.password}
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
                            name="confirmarPassword"
                            className="form-control"
                            placeholder="********"
                            value={formData.confirmarPassword}
                            onChange={handleChange}
                            style={{
                                borderColor: "var(--border-color-dark)"
                            }}
                        />
                    </div>

                    {error && (
                        <div className="alert alert-danger">
                            {error}
                        </div>
                    )}

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
        </div>
    );
}

export default Registrar;