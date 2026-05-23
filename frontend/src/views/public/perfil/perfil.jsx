import { useState, useEffect } from "react";
import { buscarContadores, buscarPerfil, buscarCalificaciones } from "../../../services/perfilService.js";
import { useAuth } from "@/contexts/userContext.jsx";
import Button from "@/components/ui/button/button.jsx";
import ConfirmModal from "@/components/ui/confirm-modal/confirm-modal.jsx";
import { useToast } from "@/contexts/toastContext.jsx";
import Paginacion from "@/components/ui/paginacion/paginacion.jsx";
import { useError } from "@/contexts/errorContext.jsx";
import { truncarADosDecimales } from "@/utils/estandarizar.jsx";
import "./perfil.css";

const renderStars = (score) => {
    const fullStars = Math.floor(score);
    const emptyStars = 5 - fullStars;

    return (
        <>
            {"★".repeat(fullStars)}
            {"☆".repeat(emptyStars)}
        </>
    );
};

const Perfil = () => {
    const [showModal, setShowModal] = useState(false);
    const [perfil, setPerfil] = useState({});
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingNotificaciones, setLoadingNotificaciones] = useState(false);
    const [stats, setStats] = useState([]);
    const [pagina, setPagina] = useState(1);
    const [filtros, setFiltros] = useState({});
    const [showConfirmModal, setShowConfirmModal] = useState(false);

    const { handleError } = useError();
    const { showToast } = useToast();
    const { user, cerrarSesion } = useAuth();

    const perfilId = user?.perfil_id;

    const manejarCierreDeSesion = () => {
        try {
            cerrarSesion();
            showToast("Cierre de sesion exitoso", "success");
        } catch (error) {
            showToast("Error al cerra la sesion", "error");
        }
    };

    useEffect(() => {
        const cargar = async () => {
            try {
                setLoading(true);

                const perfilData = await buscarPerfil();
                const statsData = await buscarContadores();

                setPerfil(perfilData);
                setStats(statsData);
            } catch (error) {
                handleError(error, () => {});
            } finally {
                setLoading(false);
            }
        };

        cargar();
    }, [handleError]);

    useEffect(() => {
        const cargarCalificaciones = async () => {
            try {
                setLoadingNotificaciones(true);

                const calificacionesApi = await buscarCalificaciones({
                    ...filtros,
                    pagina,
                    limite: 10,
                });

                setReviews(calificacionesApi);
            } catch (error) {
                handleError(error, () => {});
            } finally {
                setLoadingNotificaciones(false);
            }
        };

        cargarCalificaciones();
    }, [filtros, pagina, handleError]);

    const promedio = truncarADosDecimales(perfil?.calificacion_media);

    const guardarCambios = async () => {
        setShowModal(false);
    };

    return (
        <div className="perfil-page">

            <div className="perfil-header">
                <div
                    className="perfil-header-inner mx-auto d-flex justify-content-between align-items-center px-4 py-4"
                >
                    <div className="perfil-header-main">
                        <div className="perfil-avatar">MG</div>

                        <div className="perfil-title-wrap">
                            {loading ? (
                                <h2 className="mb-0">Cargando datos...</h2>
                            ) : (
                                <>
                                    <h2 className="perfil-title">
                                        {perfil?.nombre}
                                    </h2>
                                    <div className="perfil-rating">
                                        {renderStars(Number(promedio))} ⭐ {promedio} ({reviews?.contenido?.length ?? 0})
                                    </div>
                                </>
                            )}
                        </div>
                    </div>

                    <div className="perfil-header-actions">
                        <button
                            className="btn btn-warning perfil-edit-btn"
                            onClick={() => setShowModal(true)}
                        >
                            Editar perfil
                        </button>

                        {perfilId != null && (
                            <Button
                                label={"Cerrar sesion"}
                                onClick={() => setShowConfirmModal(true)}
                                className="perfil-logout-btn"
                            />
                        )}
                    </div>
                </div>
            </div>

            <div className="perfil-stats">
                <div className="perfil-stats-inner mx-auto d-flex text-center text-white">
                    {stats.map((stat, i) => (
                        <div key={i} className="perfil-stat">
                            <div className="perfil-stat-value">
                                {stat.valor}
                            </div>
                            <div className="perfil-stat-label">
                                {stat.nombre}
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            <div className="perfil-content mx-auto mt-4 px-3">
                <div className="mb-4">
                    <h5 className="mb-3 fw-bold">Últimas calificaciones</h5>

                    <div className="d-flex flex-column gap-3">
                        {loadingNotificaciones ? (
                            <p className="text-muted">Cargando reseñas...</p>
                        ) : reviews?.contenido?.length === 0 ? (
                            <p className="text-muted">Este usuario no tiene reseñas disponibles</p>
                        ) : (
                            reviews?.contenido?.map((r, i) => (
                                <div key={i} className="perfil-review-card">
                                    <div className="perfil-review-avatar">
                                        {r.iniciales}
                                    </div>

                                    <div className="perfil-review-text">
                                        <strong>{r.nombre}</strong>
                                        <div>{renderStars(r.valor)} {r.valor}/5</div>
                                        <p className="mb-0 text-muted" style={{ fontSize: "0.9rem" }}>
                                            "{r.descripcion}"
                                        </p>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>

                <Paginacion
                    page={pagina}
                    totalPages={reviews?.cantidad_de_paginas ?? 1}
                    onChange={setPagina}
                />
            </div>

            {showModal && (
                <div
                    className="perfil-modal-overlay"
                    onClick={() => setShowModal(false)}
                >
                    <div
                        className="perfil-modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <button
                            className="perfil-modal-close"
                            onClick={() => setShowModal(false)}
                        >
                            ✖
                        </button>

                        <h5 className="perfil-modal-title">Editar perfil</h5>

                        <label className="perfil-modal-label">Nombre</label>
                        <input
                            className="form-control perfil-modal-input"
                            value={perfil?.nombre ?? ""}
                            onChange={(e) =>
                                setPerfil((prev) => ({ ...prev, nombre: e.target.value }))
                            }
                        />

                        <label className="text-muted">@messi_g</label>

                        <button
                            className="btn mt-3 w-100 perfil-modal-save"
                            onClick={guardarCambios}
                        >
                            Guardar cambios
                        </button>
                    </div>
                </div>
            )}

            <ConfirmModal
                show={showConfirmModal}
                titulo={"Esta seguro que quiere cerrar su sesion?"}
                labelConfirmar={"Aceptar"}
                onConfirmar={manejarCierreDeSesion}
                onCancelar={() => setShowConfirmModal(false)}
            />
        </div>
    );
};

export default Perfil;